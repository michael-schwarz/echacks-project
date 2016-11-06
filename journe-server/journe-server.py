import collections
import os
import hashlib, uuid

from flask import send_file

import credentials

from flask import Flask, render_template, json
from flask.ext.mysql import MySQL

from flask import Flask, request, redirect, url_for

app = Flask(__name__)
mysql = MySQL()

# MySQL configurations
app.config['MYSQL_DATABASE_USER'] = os.environ['MYSQL_DATABASE_USER']
app.config['MYSQL_DATABASE_PASSWORD'] = os.environ['MYSQL_DATABASE_PASSWORD']
app.config['MYSQL_DATABASE_DB'] = os.environ['MYSQL_DATABASE_DB']
app.config['MYSQL_DATABASE_HOST'] = os.environ['MYSQL_DATABASE_HOST']
mysql.init_app(app)

# Image folder configuration
UPLOAD_FOLDER = 'image/'
CURRENT_DIRECTORY = os.path.dirname(os.path.abspath(__file__)) + '/'
JPG_EXT = ".jpg"
ALLOWED_EXTENSIONS = set(['jpg', 'jpeg', 'JPG', 'JPEG'])  # 'png', 'gif'
DEFAULT_IMG = CURRENT_DIRECTORY + 'default-img' + JPG_EXT
#app.use_x_sendfile = True


@app.route('/')
def main():
    return render_template('index.html')

# GET PICTURE
@app.route('/getPicture/<id>/')
def getPicture(id):
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT id FROM picture WHERE id = %s"
    cur.execute(query, id)
    data = cur.fetchone()
    filename = ''
    if data is not None and len(data) > 0:
        filename = CURRENT_DIRECTORY + UPLOAD_FOLDER + str(data[0]) + JPG_EXT

    conn.close()

    if os.path.isfile(filename):
        return send_file(filename, mimetype='image/jpeg')
    else:
        print "Failed to get image \"" + filename + "\". Return default image."
        return send_file(DEFAULT_IMG, mimetype='image/jpeg')


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


# SAVE PICTURE
@app.route('/savePicture/<user_id>/<lat>/<lng>/', methods=['POST'])
def savePicture(user_id, lat, lng):
    params = (user_id, lat, lng)
    # user_id = int(user_id)
    # lat = float(lat)
    # lng = float(lng)
    if request.method == 'POST':
        # check if the post request has the file part
        if 'imagefile' not in request.files:
            return 'No file part'

        conn = mysql.connect()
        cur = conn.cursor()
        query = "INSERT INTO picture(user_id, lat, lng) VALUES(%s, %s, %s)"
        cur.execute(query, params)
        id = cur.lastrowid
        conn.commit()
        conn.close()

        file = request.files['imagefile']
        file.filename = str(id) + JPG_EXT
        file.save(CURRENT_DIRECTORY + UPLOAD_FOLDER + file.filename)
        return "Success"

    return "End of function saveImage, no successful!!"

# GET PICTURE BY COORDINATES
@app.route('/getPicturesByCoords/<lat>/<lng>/<radius>/')
def getPicturesByCoords(lat, lng, radius):
    latMin = float(lat) - float(radius)
    latMax = float(lat) + float(radius)
    lngMin = float(lng) - float(radius)
    lngMax = float(lng) + float(radius)
    params = (latMin, latMax, lngMin, lngMax)

    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT id, user_id, lat, lng FROM picture WHERE lat > %s AND lat < %s AND lng > %s AND lng < %s"
    cur.execute(query, params)
    data = cur.fetchall()
    conn.close()

    return json.jsonify(convertCoordsToJson(data))

def convertCoordsToJson(rows):
    objects_list = []
    for row in rows:
        d = collections.OrderedDict()
        d['id'] = row[0]
        d['userId'] = row[1]
        d['lat'] = row[2]
        d['lng'] = row[3]
        objects_list.append(d)

    returnObj = collections.OrderedDict()
    returnObj['listOfPictures'] = objects_list

    return returnObj

# GET USER BY ID
@app.route('/user/<id>/')
def user(id):
    if type(id) is not int:
        return generateJsonError("Invalid input for user id: \"" + id)

    params = (id)
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT id, email, points FROM user WHERE id = %s"
    cur.execute(query, params)
    data = cur.fetchone()
    conn.close()

    if data is not None:
        return json.jsonify({"id": data[0], "email": data[1], "points": data[2]})
    else:
        return json.jsonify({})


# SIGN UP
@app.route('/createUser/<email>/<password>/')
def createUser(email, password):
    salt = uuid.uuid4().hex
    hashed_password = getPasswordHash(password, salt)
    params = (email, hashed_password, salt)

    conn = mysql.connect()
    cur = conn.cursor()
    query = "INSERT INTO user(email, password, salt) VALUES(%s, %s, %s)"
    cur.execute(query, params)
    id = cur.lastrowid
    conn.commit()
    conn.close()

    return user(id)

# CHECK IF USER AND PASSWORD MATCH
@app.route('/login/<email>/<userPass>/')
def login(email, userPass):
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT salt, password, id FROM user WHERE email = %s"
    cur.execute(query, email)
    data = cur.fetchone()
    conn.close()

    if data is not None:
        password_matchtest = getPasswordHash(userPass, data[0])

        if password_matchtest == data[1]:
            return user(data[2])

    return json.jsonify({})


def getPasswordHash(password, salt):
    return hashlib.sha512(password + salt).hexdigest()


@app.route('/hello/')
def hello():
    return 'Hello, World!!!'


if __name__ == '__main__':
    app.run(host='0.0.0.0',threaded=True)
