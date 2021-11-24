#include <FileCipher.hpp>

#include <fstream>
#include <cstring>
#include <algorithm>
#include <stdexcept>

#include <openssl/aes.h>
#include <openssl/rand.h>
#include <openssl/pkcs12.h>
#include <openssl/err.h>
#include <openssl/md5.h>
#include <openssl/pem.h>


Cipher::Cipher(AesOperationalMode aesMode)
: key(AES_BLOCK_SIZE)
{
    switch(aesMode)
    {
        case ENCRYPT_CRT:
            encryptor = new CipherAesCrtModeBuilder();
        break;
        case ENCRYPT_OFB:
            encryptor = new CipherAesOfbModeBuilder();
        break;
        case ENCRYPT_CBC:
            encryptor = new CipherAesCbcModeBuilder();
        break;
        default:
            throw std::logic_error("Wrong type of AES operation mode has been chosen");
    }
}

void Cipher::setKey(const char* key)
{
	encryptor->setKey(key);
}

void Cipher::init(const unsigned char* iv)
{
    encryptor->setIv(iv);
}

void Cipher::setBuffer(unsigned int size)
{
    buffer = std::vector<unsigned char>(size, 0);
}

void Cipher::getKeyFromKeystore(const char* keystore_path, const char* password)
{
    FILE *keystore;
    EVP_PKEY *pkey;
    X509 *cert;
    STACK_OF(X509) *ca = NULL;
    PKCS12 *p12;
    unsigned char* ptr = NULL;
    unsigned char key[16];
    OpenSSL_add_all_algorithms();
    ERR_load_crypto_strings();
    if (!(keystore = fopen(keystore_path, "rb")))
        throw std::logic_error("Cannot open keystore file.");
    p12 = d2i_PKCS12_fp(keystore, NULL);
    fclose (keystore);
    if (!p12)
        throw std::logic_error("Cannot load key from file.");
    if (!PKCS12_parse(p12, password, &pkey, &cert, &ca))
        throw std::logic_error("Cannot get the key from keystore file with provided password.");
    PKCS12_free(p12);
    BIO *bio = BIO_new(BIO_s_mem());
    if(bio == NULL)
        throw std::logic_error("Memory exception during openSSL BIO buffer creation.");
    if(PEM_write_bio_PrivateKey(bio, pkey, NULL, 0, 0, NULL, NULL) != 1)
        throw std::logic_error("Cannot write encrypted key to the memory buffer.");
    int priv_key_length = BIO_get_mem_data(bio, &ptr);
    MD5(ptr, priv_key_length, key);
    setKey((const char*)key);
    sk_X509_pop_free(ca, X509_free);
    X509_free(cert);
    EVP_PKEY_free(pkey);
}

void Cipher::readInputFileToBuffer(unsigned int filePosition, unsigned int numberOfBytesToRead)
{
    if(buffer.size() < numberOfBytesToRead)
        throw std::logic_error("Given size of expected read data is bigger than buffer size.");
	inputFileHandler.seekg(filePosition);
	inputFileHandler.read((char*)buffer.data(), numberOfBytesToRead);
}

void Cipher::writeToOutputFile(unsigned int start, unsigned int end)
{
    outputFileHandler.write((const char*)buffer.data() + start, end - start);
}

void Cipher::enableEncryptMode(const char* inputFileName, const char* outputFileName)
{
    unsigned int inputFileSize;
    unsigned char iv[8];
	inputFileHandler.open(inputFileName, std::ios::binary | std::ios::in);
	outputFileHandler.open(outputFileName, std::ios::binary | std::ios::out | std::ios::trunc);
	if(inputFileHandler.fail())
	    throw std::logic_error(std::string("Could not find file: #") + inputFileName + "#");
	inputFileHandler.seekg(0, std::ios::end);
    inputFileSize = inputFileHandler.tellg();
    inputFileHandler.seekg(0, std::ios::beg);
    RAND_bytes(iv, 8);
    init(iv);
    buffer[0] = 'O'; buffer[1] = 'K';
    decryptedFilePadding = inputFileSize % 16 == 0 ? 0 : 16 - (inputFileSize % 16);
    decryptedFilePadding = encryptor->getMode() == ENCRYPT_CBC ? (unsigned int) decryptedFilePadding : 0;
    buffer[2] = decryptedFilePadding;
    std::copy(iv, iv+8, buffer.begin() + 3);
    writeToOutputFile(0,11);
}

void Cipher::enableDecryptMode(const char* inputFileName, const char* outputFileName)
{
	inputFileHandler.open(inputFileName, std::ios::binary | std::ios::in);
	outputFileHandler.open(outputFileName, std::ios::binary | std::ios::out | std::ios::trunc);
	if(inputFileHandler.fail())
	    throw std::logic_error(std::string("Could not find file: ") + inputFileName);
    readInputFileToBuffer(0, 11);
    if(buffer[0] != 'O' || buffer[1] != 'K')
        throw std::logic_error("The specified file has incorrect format.");
    init(buffer.data() + 3);
    decryptedFilePadding = encryptor->getMode() == ENCRYPT_CBC ? (unsigned int) buffer[2] : 0;
}

void Cipher::encryptFileToFile()
{
    for(int filePosition=0; (unsigned int)inputFileHandler.gcount() == (unsigned int)buffer.size() || filePosition == 0; filePosition += buffer.size())
    {
        readInputFileToBuffer(filePosition, buffer.size());
        encryptor->encryptDataInBuffer(inputFileHandler.gcount(), buffer.data());
        if((unsigned int)inputFileHandler.gcount() < (unsigned int)buffer.size())
            writeToOutputFile(0, inputFileHandler.gcount() + decryptedFilePadding);
        else
            writeToOutputFile(0, inputFileHandler.gcount());
    }
}

void Cipher::decryptFileToFile()
{
    for(int filePosition=0; (unsigned int)inputFileHandler.gcount() == (unsigned int)buffer.size() || filePosition == 0; filePosition += buffer.size())
    {
        const int formatHeader = 2;
        const int paddingInformation = 1;
        const int ivBytes = 8;
        readInputFileToBuffer(filePosition + formatHeader + paddingInformation + ivBytes, buffer.size());
        encryptor->decryptDataInBuffer(inputFileHandler.gcount(), buffer.data());
        if((unsigned int)inputFileHandler.gcount() < (unsigned int)buffer.size())
            writeToOutputFile(0, inputFileHandler.gcount() - decryptedFilePadding);
        else
            writeToOutputFile(0, inputFileHandler.gcount());
    }
}
