import collections
import os
import hashlib, uuid

from flask import send_file

import credentials

from flask import Flask, render_template, json
from flask.ext.mysql import MySQL

from flask import Flask, request, redirect, url_for
from werkzeug.utils import secure_filename


# +++++++++++++++++++++++++++
#   DEFINITIONS
# +++++++++++++++++++++++++++

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


# +++++++++++++++++++++++++++
#   HELPER FUNCTIONS
# +++++++++++++++++++++++++++

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

def getPasswordHash(password, salt):
    return hashlib.sha512(password + salt).hexdigest()

def generateJsonError(errorMsg):
    return json.jsonify({"errorReason": errorMsg})

def ignore_exception(IgnoreException=Exception, DefaultVal=None):
    def dec(function):
        def _dec(*args, **kwargs):
            try:
                return function(*args, **kwargs)
            except IgnoreException:
                return DefaultVal
        return _dec
    return dec

sint = ignore_exception(ValueError)(int)


# +++++++++++++++++++++++++++
#   ROUTINGS
# +++++++++++++++++++++++++++

# MAIN
@app.route('/')
def main():
    return render_template('index.html')

@app.route('/hello/')
def hello():
    return 'Hello, World!!!'


# GET PICTURE
@app.route('/getPicture/<id>/')
def getPicture(id):
    if not id or id.isspace():
        return generateJsonError("Id is empty!")

    if sint(id) is None:
        return generateJsonError('Invalid input for image id: "' + id + '"')

    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT id FROM picture WHERE id = %s"
    cur.execute(query, id)
    data = cur.fetchone()
    conn.close()

    filename = ''
    if data is not None and len(data) > 0:
        filename = CURRENT_DIRECTORY + UPLOAD_FOLDER + id + JPG_EXT

    if os.path.isfile(filename):
        return send_file(filename, mimetype='image/jpeg')
    else:
        print 'Failed to get image "' + id + '". Return default image.'
        return send_file(DEFAULT_IMG, mimetype='image/jpeg')


# SAVE PICTURE
@app.route('/savePicture/<userId>/<lat>/<lng>/', methods=['POST'])
def savePicture(userId, lat, lng):
    if request.method != 'POST':
        return generateJsonError('Saving picture just works over POST requests.')

    if not userId or userId.isspace():
        return generateJsonError('User id is empty!')
    if sint(userId) is None:
        return generateJsonError('Invalid input for user id: "' + userId + '"')

    if not lat or lat.isspace():
        return generateJsonError('Latitude value is empty!')
    if sint(lat) is None:
        return generateJsonError('Invalid input for latitude value: "' + lat + '"')

    if not lng or lng.isspace():
        return generateJsonError('Longitude value is empty!')
    if sint(lng) is None:
        return generateJsonError('Invalid input for longitude value: "' + lng + '"')

    if 'imagefile' not in request.files:
        return generateJsonError('No image send in the POST request.')

    file = request.files['imagefile']

    params = (userId, lat, lng)
    conn = mysql.connect()
    cur = conn.cursor()
    query = "INSERT INTO picture(user_id, lat, lng) VALUES(%s, %s, %s)"
    cur.execute(query, params)
    id = cur.lastrowid
    conn.commit()
    conn.close()

    file.filename = str(id) + JPG_EXT
    file.save(CURRENT_DIRECTORY + UPLOAD_FOLDER + file.filename)

    return json.jsonify({"id": id, "userId": userId, "lat": lat, "lng": lng})


# GET PICTURE BY COORDINATES
@app.route('/getPicturesByCoords/<lat>/<lng>/<radius>/')
def getPicturesByCoords(lat, lng, radius):
    if not lat or lat.isspace():
        return generateJsonError('Latitude value is empty!')
    if sint(lat) is None:
        return generateJsonError('Invalid input for latitude value: "' + lat + '"')

    if not lng or lng.isspace():
        return generateJsonError('Longitude value is empty!')
    if sint(lng) is None:
        return generateJsonError('Invalid input for longitude value: "' + lng + '"')

    if not radius or radius.isspace():
        return generateJsonError('Radius value is empty!')
    if sint(radius) is None:
        return generateJsonError('Invalid input for radius value: "' + radius + '"')

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

    objects_list = []
    for row in data:
        d = collections.OrderedDict()
        d['id'] = row[0]
        d['userId'] = row[1]
        d['lat'] = row[2]
        d['lng'] = row[3]
        objects_list.append(d)

    returnList = collections.OrderedDict()
    returnList['listOfPictures'] = objects_list

    return json.jsonify(returnList)


# GET USER BY ID
@app.route('/user/<id>/')
def user(id):
    if type(id) is not int and (not id or id.isspace()):
        return generateJsonError('Id value is empty!')
    if sint(id) is None:
        return generateJsonError('Invalid input for user id: "' + id + '"')

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
        return generateJsonError("No user with id " + id + " found.")


# SIGN UP
@app.route('/createUser/<email>/<password>/')
def createUser(email, password):
    if not email or email.isspace():
        return generateJsonError("Email is empty!")

    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT id FROM user WHERE email = %s"
    cur.execute(query, email)
    data = cur.fetchone()
    conn.close()

    if data is not None:
        return generateJsonError('User with email "' + email + '" already exists.')

    if not password or password.isspace():
        return generateJsonError("Password is empty!")

    if len(password) < 6:
        return generateJsonError("Password must have at least 6 characters.")

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


# LOGIN CHECK IF USER AND PASSWORD MATCH
@app.route('/login/<email>/<password>/')
def login(email, password):
    if not email or email.isspace():
        return generateJsonError("Email is empty!")

    if not password or password.isspace():
        return generateJsonError("Password is empty!")

    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT salt, password, id FROM user WHERE email = %s"
    cur.execute(query, email)
    data = cur.fetchone()
    conn.close()

    if data is not None:
        passwordMatchtest = getPasswordHash(password, data[0])

        if passwordMatchtest == data[1]:
            return user(data[2])
        else:
            return generateJsonError("Email und password do not match!")
    else:
        return generateJsonError('No user with email "' + email + '" found.')


# +++++++++++++++++++++++++++
#   RUN APP
# +++++++++++++++++++++++++++
if __name__ == '__main__':
    app.run(host='0.0.0.0', threaded=True)