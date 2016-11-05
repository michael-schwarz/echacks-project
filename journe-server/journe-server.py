import os
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


@app.route('/picture/<id>/')
def picture(id):
    conn = mysql.connect()
    cur = conn.cursor()
    query = "SELECT * FROM picture WHERE id = %s"
    cur.execute(query, id)
    data = cur.fetchall()

    return json.jsonify(data)



if __name__ == '__main__':
    app.run()
