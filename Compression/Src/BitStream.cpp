#include <fstream>
#include <iostream>
#include <BitStream.hpp>


bool BitStream::eof()
{
    return m_file->eof() && m_counter == 0;
}

void BitStream::setFile(std::fstream* p_file)
{
    m_file = p_file;
}

unsigned char BitStream::getBit()
{
    if(m_counter == 0)
    {
        m_pendingBits = 0;
        if(m_file->read((char*)&m_pendingBits, sizeof(char)))
            m_counter = 8;
        else
            m_counter = 1;
    }
    m_counter--;
    unsigned char bit = (m_pendingBits >> m_counter) & 1;
    return bit;
}

void BitStream::putBit(const unsigned char p_bit)
{
    m_pendingBits = (m_pendingBits << 1) + (p_bit & 1);
    m_counter++;
    if(m_counter == 8)
    {
        m_file->write((const char*)&m_pendingBits, sizeof(char));
        m_counter = 0;
        m_pendingBits = 0;
    }
}

void BitStream::forceWrite()
{
    if (m_counter > 0)
    {
        m_pendingBits <<= 8-m_counter;
        m_file->write((const char*)&m_pendingBits, sizeof(char));
    }
    m_counter = 0;
    m_pendingBits = 0;
}
