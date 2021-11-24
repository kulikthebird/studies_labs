import socket
import ssl
import httplib
import urllib
import json
from obliviousTransfer import OTProtocolClient
from oneItemRequest import OneItemReqClient


class Client(object):
    def __init__(self, certFile, keyFile, trustedThirdParty):
        self.__sslContext = ssl.create_default_context()
        self.__sslContext.load_cert_chain(certfile=certFile, keyfile=keyFile)
        self.__sslContext.check_hostname = False
        self.__sslContext.verify_mode = ssl.CERT_REQUIRED
        self.__sslContext.load_verify_locations(capath=trustedThirdParty)
        self.__serversDict = {}
        self.__oneItemReqClient = OneItemReqClient()

    def beginOTProtocol(self, host, port, request):
        if (host, port) not in self.__serversDict.keys():
            self.__serversDict[(host, port)] = OneItemReqClient()
        __oneItemClient = self.__serversDict[(host, port)]
        connection = httplib.HTTPSConnection(str(host)+":"+str(port), context=self.__sslContext)
        query = __oneItemClient.handleMessage({'MessageType': 'BeginOneItemRequest', 'Item': request})
        connection.request("POST", "/", urllib.urlencode(query))
        while not __oneItemClient.isFinished():
            content = json.loads(connection.getresponse().read())
            response = __oneItemClient.handleMessage(content)
            if response != None:
                connection.request("POST", "/", urllib.urlencode(response))
        connection.close()
        return __oneItemClient.getResult()
