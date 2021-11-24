from client import Client
import BaseHTTPServer


def form(additional_info = ''):
    return """
            <html><body> 
            <form action="/" method="post"> 
                <input type="text" name="host" value="127.0.0.1:6002">
                <input type="text" name="request">
                <input type="submit">
            </form>
            <h4>""" + additional_info + """</h4>
            </body></html>
        """

class UserInterfaceHttpRequestHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.end_headers()
        self.wfile.write(form())

    def do_POST(self):
        length = int(self.headers['Content-Length'])
        postDict = {}
        for x in self.rfile.read(length).decode('utf-8').split('&'):
            keyVal = x.split('=')
            postDict[keyVal[0]] = keyVal[1]
        self.send_response(200)
        self.end_headers()
        if 'request' in postDict.keys() and 'host' in postDict.keys():
            client = Client('bankA/cert.pem', 'bankA/private.pem', 'trustedThirdParty/')
            host = postDict['host'].split('%3A')
            string = client.beginOTProtocol(host[0], int(host[1]), postDict['request'])
            string = 'Element is present in requested host database' if string == 1 else \
                        'Element is absent in requested host database'
        else:
            string = 'Wrong data!'
        self.wfile.write(form(string))
