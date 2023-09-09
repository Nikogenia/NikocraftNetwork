import struct
import socket
import base64

import rsa
import aes

from message import *

def read_utf():
    utf_length = struct.unpack('>H', s.recv(2))[0]
    return s.recv(utf_length)

def write_utf(data):
    s.send(struct.pack('>H', len(data)))
    s.send(data)

def send(string):
    if s.fileno() == -1:
        init()
    write_utf(aes.encrypt(aes_key, string).encode("utf-8"))

def recv():
    if s.fileno() == -1:
        init()
    return aes.decrypt(aes_key, read_utf())

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

private_key = rsa.generate_private_key()
public_key = rsa.generate_public_key(private_key)

export = rsa.export_key_to_bytes(public_key)
decoded = export.decode("utf-8")
fixed = decoded.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "")

aes_key = ""

def init():

    global aes_key
    global s

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    s.connect(("localhost", 42420))

    write_utf("NikocraftNetwork-0.0.1-1.0".encode("utf-8"))

    print(read_utf())

    write_utf(fixed.encode("utf-8"))

    aes_key = rsa.decrypt_bytes(private_key, base64.b64decode(read_utf())).decode("utf-8")

    write_utf(aes.encrypt(aes_key, format(AUTH_ID) + "control").encode("utf-8"))
    write_utf(aes.encrypt(aes_key, format(AUTH_KEY) + "vtfopUiHdoay6HisHmFYy8CHXiFYtTgU").encode("utf-8"))

    print(aes.decrypt(aes_key, read_utf()))
