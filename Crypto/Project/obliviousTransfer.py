import random
import math


def fastModPow(base, power, m):
    result = 1
    while power > 0:
        if power % 2 == 1:
            result = (result * base) % m
        power = power / 2
        base = (base * base) % m
    return result


class OTProtocolTool(object):
    def __init__(self):
        self.__public = -1
        self.__private = -1
        self.__modulus = -1

    def setPublicKey(self, public, modulus):
        self.__public = public
        self.__modulus = modulus

    def setPrivateKey(self, private):
        self.__private = private

    def isKeySet(self):
        return self.__modulus != -1

    def generateTwoRandomStrings(self, stringSize):
        return [random.randint(1, stringSize), random.randint(1, stringSize)]

    def generateUserChoice(self, firstRandomNumber, secondRandomNumber, userRandomNumber, choice):
        number = 0
        if choice == 0:
            number = firstRandomNumber
        else:
            number = secondRandomNumber
        return (number + fastModPow(userRandomNumber, self.__public, self.__modulus)) % self.__modulus

    def hideTwoValuesInRandomNumbers(self, firstRandomNumber, secondRandomNumber, userNumber, secretVal1, secretVal2):
        return  [
                    secretVal1 + fastModPow((userNumber-firstRandomNumber), self.__private, self.__modulus),
                    secretVal2 + fastModPow((userNumber-secondRandomNumber), self.__private, self.__modulus)
                ]
    
    def decryptHiddenUserChoice(self, firstNumber, secondNumber, userRandomNumber, choice):
        number = 0
        if choice == 0:
            number = firstNumber
        else:
            number = secondNumber
        return number - userRandomNumber


class OTProtocolServer(object):
    def __init__(self):
        self.__otProtocolTool = OTProtocolTool()
        self.__public = 0
        self.__modulus = 0
        self.currentRound = 0
        self.secretValuesList = []

    def handleMessage(self, message):
        if self.currentRound >= len(self.secretValuesList):
            return {'MessageType':'ERROR'}
        if message['MessageType'] == 'RandomNumberRequest':
            self.randomStrings = self.__otProtocolTool.generateTwoRandomStrings(256)
            result = {'MessageType':'SetRandomNumbers', 
                'firstNumber': str(self.randomStrings[0]), 'secondNumber': str(self.randomStrings[1])}
            if 'PublicKeyRequest' in message.keys():
                result['public'] = str(self.__public)
                result['modulus'] = str(self.__modulus)
            return result
        elif message['MessageType'] == 'EncMessagesRequest':
            hiddenValues = self.__otProtocolTool.hideTwoValuesInRandomNumbers(
                self.randomStrings[0], self.randomStrings[1], int(message['UserChoice']),
                self.secretValuesList[self.currentRound][0], self.secretValuesList[self.currentRound][1])
            self.currentRound += 1
            return {'MessageType':'SetEncryptedMessages', 
                    'serverNumber1': str(hiddenValues[0]), 'serverNumber2': str(hiddenValues[1])}
        return {'MessageType':'ERROR'}

    def setSecretValues(self, a, b):
        self.secretValuesList = [(a,b)]
        self.currentRound = 0

    def setListOfSecretValues(self, secretValuesList):
        self.secretValuesList = secretValuesList
        self.currentRound = 0

    def setPrivateKey(self, public, private, modulus):
        self.__public = public
        self.__modulus = modulus
        self.__otProtocolTool.setPublicKey(public, modulus)
        self.__otProtocolTool.setPrivateKey(private)

    def isFinished(self):
        return self.currentRound >= len(self.secretValuesList)

class OTProtocolClient(object):
    def __init__(self):
        self.__otProtocolTool = OTProtocolTool()
        self.__finished = True

    def handleMessage(self, message):
        if message['MessageType'] == 'StartExchange':
            self.__finished = False
            self.choice = message['Choice']
            if not self.__otProtocolTool.isKeySet():
                return {'MessageType':'RandomNumberRequest', 'PublicKeyRequest': 'True'}
            else:
                return {'MessageType':'RandomNumberRequest'}
        elif message['MessageType'] == 'SetRandomNumbers':
            if 'public' in message.keys() and 'modulus' in message.keys():
                self.__otProtocolTool.setPublicKey(int(message['public']), int(message['modulus']))
            self.userRandomNumber = random.randint(1, 1234)
            userChoiceEnc = self.__otProtocolTool.generateUserChoice(
                int(message['firstNumber']), int(message['secondNumber']), self.userRandomNumber, self.choice)
            return {'MessageType':'EncMessagesRequest', 'UserChoice': str(userChoiceEnc)}
        elif message['MessageType'] == 'SetEncryptedMessages':
            self.__result = self.__otProtocolTool.decryptHiddenUserChoice(
            int(message['serverNumber1']), int(message['serverNumber2']), self.userRandomNumber, self.choice)
            self.__finished = True
        return None

    def getResult(self):
        return self.__result

    def isFinished(self):
        return self.__finished
