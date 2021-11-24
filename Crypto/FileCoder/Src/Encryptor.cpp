#include <Encryptor.hpp>
#include <openssl/aes.h>


CipherAesOperationMode::CipherAesOperationMode() 
: iv(AES_BLOCK_SIZE), ecount(AES_BLOCK_SIZE)
{}

void CipherAesOperationMode::setKey(const char* key)
{
    AES_set_encrypt_key((const unsigned char*)key, 128, &aesKeyEnc);
    AES_set_decrypt_key((const unsigned char*)key, 128, &aesKeyDec);
}

void CipherAesOperationMode::setIv(const unsigned char* p_iv)
{
    num = 0;
    std::copy(p_iv, p_iv+8, iv.begin());
    std::fill(iv.begin()+8, iv.end(), 0);
    std::fill(ecount.begin(), ecount.end(), 0);
}

void CipherAesCrtModeBuilder::encryptDataInBuffer(int size, unsigned char *buffer)
{
    AES_ctr128_encrypt(buffer, buffer, size, &aesKeyEnc, iv.data(), ecount.data(), &num);
}

void CipherAesCrtModeBuilder::decryptDataInBuffer(int size, unsigned char *buffer)
{
    AES_ctr128_encrypt(buffer, buffer, size, &aesKeyEnc, iv.data(), ecount.data(), &num);
}

void CipherAesOfbModeBuilder::encryptDataInBuffer(int size, unsigned char *buffer)
{
    AES_ofb128_encrypt(buffer, buffer, size, &aesKeyEnc, iv.data(), (int*)&num);
}

void CipherAesOfbModeBuilder::decryptDataInBuffer(int size, unsigned char *buffer)
{
    AES_ofb128_encrypt(buffer, buffer, size, &aesKeyEnc, iv.data(), (int*)&num);
}

void CipherAesCbcModeBuilder::encryptDataInBuffer(int size, unsigned char *buffer)
{
    AES_cbc_encrypt(buffer, buffer, size, &aesKeyEnc, iv.data(), AES_ENCRYPT);
}

void CipherAesCbcModeBuilder::decryptDataInBuffer(int size, unsigned char *buffer)
{
    AES_cbc_encrypt(buffer, buffer, size, &aesKeyDec, iv.data(), AES_DECRYPT);
}

AesOperationalMode CipherAesCrtModeBuilder::getMode()
{
    return ENCRYPT_CRT;
}

AesOperationalMode CipherAesOfbModeBuilder::getMode()
{
    return ENCRYPT_OFB;
}

AesOperationalMode CipherAesCbcModeBuilder::getMode()
{
    return ENCRYPT_CBC;
}
