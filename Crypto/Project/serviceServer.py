import BaseHTTPServer
import urlparse
from obliviousTransfer import OTProtocolServer
from oneItemRequest import OneItemReqServer


database = ['123', '432', '345']
test = OneItemReqServer(lambda x, hash: x in [hash(y) for y in database])

class ServiceInterfaceHttpRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_POST(self):
        length = int(self.headers['Content-Length'])
        postDict = {}
        for x in self.rfile.read(length).decode('utf-8').split('&'):
            keyVal = x.split('=')
            postDict[keyVal[0]] = keyVal[1]
        self.send_response(200)
        self.end_headers()
        self.wfile.write(str(test.handleMessage(postDict)).replace("'", '"'))
