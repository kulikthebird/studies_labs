from hashlib import sha256
import random
from obliviousTransfer import OTProtocolClient
from obliviousTransfer import OTProtocolServer


class OneItemReqServer(object):
    def __init__(self):
        pass

def hash(x):
    return sha256(str(x)).hexdigest()


class OneItemReqServer(object):
    def __init__(self, itemChecker):
        self.state = self.idleMode
        self.items = []
        self.resultItems = []
        self.generatedKeys = []
        self.checkIfItemPresent = itemChecker
        self.otServer = OTProtocolServer()
        self.otServer.setPrivateKey(2093, 2422037, 2624399)
        self.isFinished = True

    def handleMessage(self, message):
        return self.state(message)

    def idleMode(self, message):
        if message['MessageType'] == 'BeginOneItemRequest':
            self.isFinished = False
            return {'MessageType': 'SendEncListOfItems'}
        elif message['MessageType'] == 'EncListOfItems':
            self.items = message['Items'].split('%23')
            self.generatedKeys = [(random.randint(0,0xFFFF), random.randint(0,0xFFFF)) for x in range(4)]
            self.resultItems = []
            for i in range(len(self.items)):
                self.resultItems += [ self.encryptItem(self.checkIfItemPresent(self.items[i], hash), i) ]
            self.state = self.extendedOt
            self.otServer.setListOfSecretValues(self.generatedKeys)
            return {'MessageType': 'KeyExchange', 'Items': '#'.join(map(str, self.resultItems))}

    def extendedOt(self, message):
        response = self.otServer.handleMessage(message)
        if self.otServer.isFinished():
            self.state = self.idleMode
            self.isFinished = True
        return response

    def encryptItem(self, item, position):
        key = ''
        for bit, keyPair in zip(map(int, list('{0:b}'.format(int(position)).zfill(4))), self.generatedKeys):
            key += str(keyPair[bit])
        return 0 if (bin(int(hash(key), 16)).count("1") % 2 == int(item)) else 1
        

class OneItemReqClient(object):
    def __init__(self):
        self.result = None
        self.itemValue = None
        self.otClient = OTProtocolClient()
        self.state = self.idleMode
        self.encItems = []
        self.keyItems = []
        self.otCounter = 0
        self.elementPosition = -1
        self.keySequence = []
        self.__isFinished = True

    def handleMessage(self, message):
        return self.state(message)

    def idleMode(self, message):
        if message['MessageType'] == 'BeginOneItemRequest':
            self.keyItems = []
            self.__isFinished = False
            self.itemValue = message['Item']
            return {'MessageType': 'BeginOneItemRequest'}
        elif message['MessageType'] == 'SendEncListOfItems':
            items = [hash(str(random.randint(0,0xFFFF))) for x in range(16)]
            self.elementPosition = random.randint(0,15)
            items[self.elementPosition] = hash(self.itemValue)
            self.keySequence = map(int, list( '{0:b}'.format(self.elementPosition).zfill(4)))
            return {'MessageType': 'EncListOfItems', 'Items': '#'.join(items) }
        elif message['MessageType'] == 'KeyExchange':
            self.encItems = message['Items'].split('#')
            self.otCounter = 0
            self.state = self.extendedOt
            return self.state({'MessageType': 'StartExchange', 'Choice': self.keySequence[self.otCounter]})
        return None

    def extendedOt(self, message):
        response = self.otClient.handleMessage(message)
        if self.otClient.isFinished():
            self.otCounter += 1
            self.keyItems += [str(self.otClient.getResult())]
            if self.otCounter < len(self.keySequence):
                return self.otClient.handleMessage({'MessageType': 'StartExchange', 'Choice': self.keySequence[self.otCounter]})
            else:
                self.state = self.idleMode
                self.__isFinished = True
                self.__checkResult()
                return None
        return response

    def __checkResult(self):
        key = ''.join(self.keyItems)
        self.result = (bin(int(hash(key), 16)).count("1") % 2) ^ int(self.encItems[self.elementPosition])

    def getResult(self):
        return self.result

    def isFinished(self):
        return self.__isFinished
