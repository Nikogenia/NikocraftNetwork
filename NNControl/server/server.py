from flask import Flask, request, jsonify, session
from flask_bcrypt import Bcrypt
from flask_cors import CORS
from flask_session import Session
from config import ApplicationConfig
from models import db, User


app = Flask(__name__)
app.config.from_object(ApplicationConfig)


bcrypt = Bcrypt(app)


CORS(app, supports_credentials=True)
server_session = Session(app)


db.init_app(app)
with app.app_context():
    db.create_all()
    if User.query.filter_by(name="admin").first() is None:
        db.session.add(User(name="admin", password=bcrypt.generate_password_hash("admin"), admin=True))
        db.session.commit()


def get_session():

    user_id = session.get("user_id")

    if not user_id:
        return jsonify({"error": "unauthorized", "message": "You need to log in!"}), 200
    
    user = User.query.filter_by(id=user_id).first()
    if user is None:
        session.pop("user_id")
        return jsonify({"error": "invalid_session", "message": "Your session is invalid! Please log in again!"}), 200
    
    return user


@app.route("/user", methods=["POST"])
def user():

    user = get_session()
    if isinstance(user, tuple):
        return user

    return jsonify({
        "error": "success",
        "id": user.id,
        "username": user.name,
        "admin": user.admin
    }), 200


@app.route("/user/password", methods=["POST"])
def user_password():

    user = get_session()
    if isinstance(user, tuple):
        return user

    password = request.json.get("password")

    if password is None:
        return jsonify({"error": "missing_fields", "message": "Missing fields! Require: [string] password"}), 200
    
    if not isinstance(password, str):
        return jsonify({"error": "invalid_field_types", "message": "Invalid field types! Require: [string] password"}), 200
    
    user.password = bcrypt.generate_password_hash(password)
    db.session.commit()

    return jsonify({
        "error": "success"
    }), 200


@app.route("/user/list", methods=["POST"])
def user_list():

    user = get_session()
    if isinstance(user, tuple):
        return user
    
    if not user.admin:
        return jsonify({"error": "missing_permission", "message": "Only administrators can list users!"}), 200

    users = []
    for user in User.query.all():
        users.append({
            "id": user.id,
            "name": user.name,
            "admin": user.admin
        })

    return jsonify({
        "error": "success",
        "users": users
    }), 200


@app.route("/user/register", methods=["POST"])
def user_register():

    user = get_session()
    if isinstance(user, tuple):
        return user

    if not user.admin:
        return jsonify({"error": "missing_permission", "message": "Only administrators can register users!"}), 200

    username = request.json.get("username")
    password = request.json.get("password")
    admin = request.json.get("admin")

    if username is None or password is None or admin is None:
        return jsonify({"error": "missing_fields", "message": "Missing fields! Require: [string] username, [string] password, [boolean] admin"}), 200
    
    if not (isinstance(username, str) and isinstance(password, str) and isinstance(admin, bool)):
        return jsonify({"error": "invalid_field_types", "message": "Invalid field types! Require: [string] username, [string] password, [boolean] admin"}), 200

    if User.query.filter_by(name=username).first() is not None:
        return jsonify({"error": "user_exists", "message": "This username is already used!"}), 200

    hashed_password = bcrypt.generate_password_hash(password)
    user = User(name=username, password=hashed_password, admin=admin)
    db.session.add(user)
    db.session.commit()

    return jsonify({
        "error": "success",
        "id": user.id,
        "username": user.name,
        "admin": user.admin
    }), 200


@app.route("/user/unregister", methods=["POST"])
def user_unregister():

    user = get_session()
    if isinstance(user, tuple):
        return user

    if not user.admin:
        return jsonify({"error": "missing_permission", "message": "Only administrators can unregister users!"}), 200

    username = request.json.get("username")

    if username is None:
        return jsonify({"error": "missing_fields", "message": "Missing fields! Require: [string] username"}), 200
    
    if not isinstance(username, str):
        return jsonify({"error": "invalid_field_types", "message": "Invalid field types! Require: [string] username"}), 200
    
    if username == "admin":
        return jsonify({"error": "delete_admin", "message": "The default admin user cannot be deleted!"}), 200

    if User.query.filter_by(name=username).first() is None:
        return jsonify({"error": "user_not_exists", "message": "This user doesn't exist!"}), 200

    User.query.filter_by(name=username).delete()
    db.session.commit()

    return jsonify({
        "error": "success"
    }), 200


@app.route("/user/login", methods=["POST"])
def user_login():

    username = request.json.get("username")
    password = request.json.get("password")

    if username is None or password is None:
        return jsonify({"error": "missing_fields", "message": "Missing fields! Require: [string] username, [string] password"}), 200
    
    if not (isinstance(username, str) and isinstance(password, str)):
        return jsonify({"error": "invalid_field_types", "message": "Invalid field types! Require: [string] username, [string] password"}), 200

    user = User.query.filter_by(name=username).first()

    if user is None:
        return jsonify({"error": "invalid_credentials", "message": "The username or password is incorrect!"}), 200

    if not bcrypt.check_password_hash(user.password, password):
        return jsonify({"error": "invalid_credentials", "message": "The username or password is incorrect!"}), 200
    
    session["user_id"] = user.id

    return jsonify({
        "error": "success",
        "id": user.id,
        "username": user.name,
        "admin": user.admin
    }), 200


@app.route("/user/logout", methods=["POST"])
def user_logout():

    user_id = session.get("user_id")

    if not user_id:
        return jsonify({"error": "unauthorized", "message": "You need to log in!"}), 200

    session.pop("user_id")

    return jsonify({
        "error": "success"
    }), 200


if __name__ == "__main__":

    app.run(debug=True, port=8080)
