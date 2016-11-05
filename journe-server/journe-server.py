import os

from flask import send_file

import credentials

from flask import Flask, render_template, json
from flask.ext.mysql import MySQL


app = Flask(__name__)
mysql = MySQL()

# MySQL configurations
app.config['MYSQL_DATABASE_USER'] = os.environ['MYSQL_DATABASE_USER']
app.config['MYSQL_DATABASE_PASSWORD'] = os.environ['MYSQL_DATABASE_PASSWORD']
app.config['MYSQL_DATABASE_DB'] = os.environ['MYSQL_DATABASE_DB']
app.config['MYSQL_DATABASE_HOST'] = os.environ['MYSQL_DATABASE_HOST']
mysql.init_app(app)

@app.route('/')
def main():
    return render_template('index.html')

@app.route('/getPicture/<id>/')
def getPicture(id):
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT filename FROM picture WHERE id = %s"
    cur.execute(query, id)
    filename = "image/" + cur.fetchone()[0]

    conn.close()

    return send_file(filename, mimetype='image/jpeg')

@app.route('/getPictureByCoords/<lat>/<lng>/<radius>')
def getPictureByCoords(lat,lng,radius):
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

    return json.jsonify({"id":1234, "email":"blabla@tum.de", "score":4742})



if __name__ == '__main__':
    app.run(host='0.0.0.0')
