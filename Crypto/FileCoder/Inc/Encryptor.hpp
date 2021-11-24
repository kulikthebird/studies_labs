#include <openssl/aes.h>
#include <vector>


enum AesOperationalMode { ENCRYPT_NONE, ENCRYPT_CRT, ENCRYPT_OFB, ENCRYPT_CBC };


class CipherAesOperationMode
{
public:
    CipherAesOperationMode();
    ~CipherAesOperationMode();
    virtual void encryptDataInBuffer(int size, unsigned char *buffer) = 0;
    virtual void decryptDataInBuffer(int size, unsigned char *buffer) = 0;
    virtual AesOperationalMode getMode() = 0;
    void setKey(const char* key);
    void setIv(const unsigned char* iv);

protected:
    AES_KEY aesKeyEnc;
    AES_KEY aesKeyDec;
    std::vector<unsigned char> iv;
    std::vector<unsigned char> ecount;
    unsigned int num;
};

class CipherAesCrtModeBuilder : public CipherAesOperationMode
{
public:
    AesOperationalMode getMode();
    void encryptDataInBuffer(int size, unsigned char *buffer) override;
    void decryptDataInBuffer(int size, unsigned char *buffer) override;
};

class CipherAesOfbModeBuilder : public CipherAesOperationMode
{
public:
    AesOperationalMode getMode();
    void encryptDataInBuffer(int size, unsigned char *buffer) override;
    void decryptDataInBuffer(int size, unsigned char *buffer) override;
};

class CipherAesCbcModeBuilder : public CipherAesOperationMode
{
public:
    AesOperationalMode getMode();
    void encryptDataInBuffer(int size, unsigned char *buffer) override;
    void decryptDataInBuffer(int size, unsigned char *buffer) override;
};
