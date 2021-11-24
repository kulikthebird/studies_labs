

class Configurator(object):
    def __init__(self, argv):
        self.__servicePort = 0
        self.__userPort = 0
        self.__certPublicPath = ''
        self.__certPrivatePath = ''
        self.__argv = argv
        self.__databasePath = ''
        self.__trustedThirdPartyPath = ''
        self.__getConfigFromArgv()

    def __getConfigFromArgv(self):
        for i in range(len(self.__argv)):
            if self.__argv[i] == "--pub" and i+1 < len(self.__argv):
                self.__certPublicPath = self.__argv[i+1]
                i += 1
            elif self.__argv[i] == "--priv" and i+1 < len(self.__argv):
                self.__certPrivatePath = self.__argv[i+1]
                i += 1
            elif self.__argv[i] == "--db" and i+1 < len(self.__argv):
                self.__databasePath = self.__argv[i+1]
                i += 1
            elif self.__argv[i] == "--trusted" and i+1 < len(self.__argv):
                self.__trustedThirdPartyPath = self.__argv[i+1]
                i += 1
            elif self.__argv[i] == "--service-port" and i+1 < len(self.__argv):
                self.__servicePort = int(self.__argv[i+1])
                i += 1
            elif self.__argv[i] == "--user-port" and i+1 < len(self.__argv):
                self.__userPort = int(self.__argv[i+1])
                i += 1

    def getPublicKeyPath(self):
        return self.__certPublicPath
    
    def getPrivateKeyPath(self):
        return self.__certPrivatePath
    
    def getDatabasePath(self):
        return self.__databasePath

    def getTrustedThirdPartyPath(self):
        return self.__trustedThirdPartyPath

    def getServicePort(self):
        return self.__servicePort

    def getUserPort(self):
        return self.__userPort

