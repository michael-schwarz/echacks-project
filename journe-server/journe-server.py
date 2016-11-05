import os

from flask import send_file

import credentials

from flask import Flask, render_template, json
from flask.ext.mysql import MySQL

from flask import Flask, request, redirect, url_for
from werkzeug.utils import secure_filename


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
CURRENT_DIRECTORY = os.path.dirname(__file__) + '/'
ALLOWED_EXTENSIONS = set(['jpg', 'jpeg', 'JPG', 'JPEG'])  # 'png', 'gif'



@app.route('/')
def main():
    return render_template('index.html')

# GET PICTURE
@app.route('/getPicture/<id>/')
def getPicture(id):
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT filename FROM picture WHERE id = %s"
    cur.execute(query, id)
    filename = UPLOAD_FOLDER + cur.fetchone()[0]

    conn.close()

    return send_file(filename, mimetype='image/jpeg')


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

# SAVE PICTURE
@app.route('/savePicture/<user_id>/<lat>/<lng>/', methods=['POST'])
def savePicture(user_id, lat, lng):
    if request.method == 'POST':
        # check if the post request has the file part
        if 'imagefile' not in request.files:
            return 'No file part'



        file = request.files['imagefile']
        # if user does not select file, browser also
        # submit a empty part without filename
        file.filename = '1.jpg'
        file.save(CURRENT_DIRECTORY + UPLOAD_FOLDER + file.filename)
        return "Success"

    return "End of function saveImage, no successful!!"

@app.route('/getPictureByCoords/<lat>/<lng>/<radius>')
def getPictureByCoords(lat, lng, radius):
    params = (lat, lng, radius)
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT * FROM picture WHERE id = %s"
    cur.execute(query, params)
    data = cur.fetchall()
    conn.close()

    return json.jsonify(data)


@app.route('/user/<id>/')
def user(id):
    # params = (id)
    # conn = mysql.connect()
    # cur = conn.cursor()
    # query = "SELECT * FROM picture WHERE id = %s"
    # cur.execute(query, params)
    # data = cur.fetchall()
    # conn.close()

    return json.jsonify({"id": 1234, "email": "blabla@tum.de", "score": 4742})


if __name__ == '__main__':
    app.run(host='0.0.0.0')
