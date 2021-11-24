#!/usr/bin/python2

from configurator import Configurator
import server
import userServer
import serviceServer
import sys
import os


config = Configurator(sys.argv)
server.SslServer(config.getPublicKeyPath(), config.getPrivateKeyPath(), config.getUserPort(), 
    userServer.UserInterfaceHttpRequestHandler).startServer()
server.SslServer(config.getPublicKeyPath(), config.getPrivateKeyPath(), config.getServicePort(),
    serviceServer.ServiceInterfaceHttpRequestHandler, config.getTrustedThirdPartyPath()).startServer()

raw_input()
os._exit(0)
