#include <fstream>
#include <Encryptor.hpp>


class Cipher
{
public:
    Cipher(AesOperationalMode aesMode);

    void setKey(const char* key);
    void init(const unsigned char* iv);
    void setBuffer(unsigned int size);
    void getKeyFromKeystore(const char* keystore_path, const char* password);
    void readInputFileToBuffer(unsigned int filePosition, unsigned int byteNumberToRead);
    void writeToOutputFile(unsigned int start, unsigned int end);
    void enableEncryptMode(const char* inputFileName, const char* outputFileName);
    void enableDecryptMode(const char* inputFileName, const char* outputFileName);
    void encryptFileToFile();
    void decryptFileToFile();

private:
    std::vector<unsigned char> key;
    std::vector<unsigned char> buffer;
    unsigned int decryptedFilePadding;
    std::fstream inputFileHandler;
    std::fstream outputFileHandler;
    CipherAesOperationMode *encryptor;
};
