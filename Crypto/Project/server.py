import ssl
import BaseHTTPServer
from threading import Thread


class SslServer(object):
    def __init__(self, certFile, keyFile, port, httpRequestHandler, trustedThirdParty=None):
        self.__certFile = certFile
        self.__keyFile = keyFile
        self.__port = port
        self.__httpRequestHandler = httpRequestHandler
        self.__server = None
        self.__thread = None
        self.__trustedThirdParty = trustedThirdParty

    def startServer(self):
        sslContext = self._setupContext()
        if self.__trustedThirdParty != None:
            self._setupCertificateRequirement(sslContext)
        self.__server = BaseHTTPServer.HTTPServer(('localhost', self.__port), self.__httpRequestHandler)
        self.__server.socket = sslContext.wrap_socket(self.__server.socket, server_side=True)
        self.__thread = Thread(target=lambda: self.__server.serve_forever()).start()

    def stopServer(self):
        pass     # kill the thread and close the socket 

    def _setupContext(self):
        sslContext = ssl.create_default_context()
        sslContext.load_cert_chain(certfile=self.__certFile, keyfile=self.__keyFile)
        sslContext.check_hostname = False
        sslContext.verify_mode = ssl.CERT_NONE
        return sslContext

    def _setupCertificateRequirement(self, sslContext):
        sslContext.verify_mode = ssl.CERT_REQUIRED
        sslContext.load_verify_locations(capath=self.__trustedThirdParty)