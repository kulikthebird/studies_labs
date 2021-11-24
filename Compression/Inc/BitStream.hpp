#pragma once

#include <fstream>
#include <iostream>


class BitStream
{
public:
    void setFile(std::fstream* p_file);
    unsigned char getBit();
    void putBit(const unsigned char p_bit);
    void forceWrite();
    bool eof();

private:
    unsigned char m_pendingBits = 0;
    unsigned int m_counter = 0;
    std::fstream* m_file;
};
