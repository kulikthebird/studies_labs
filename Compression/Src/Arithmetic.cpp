#include <fstream>
#include <cmath>
#include <Arithmetic.hpp>
#include <BitStream.hpp>
#include <ICompress.hpp>


Arithmetic::Arithmetic(const char* p_inputFilePath, const char* p_outputFilePath)
    :    m_partialSum(m_numberOfCodeElements),
         ICompress(p_inputFilePath, p_outputFilePath)
{}

void Arithmetic::codeStream()
{
    unsigned int l_pendingBits = 0;
    unsigned char l_currentByte;
    unsigned int low = 0;
    unsigned int high = 0xFFFF;
    m_compressedFileStream.setFile(&m_outputFile);
    writeInputFileSize();
    while(m_inputFile.read((char*)&l_currentByte, sizeof(char)))
    {
        unsigned int range = high - low + 1;
        unsigned int down = m_partialSum[l_currentByte];
        unsigned int up = m_partialSum[l_currentByte+1];
        high = low + (range * up) / m_partialSum.getSumOfAllElements() - 1;
        low = low + (range * down) / m_partialSum.getSumOfAllElements();
        m_partialSum.updatePartialSum(l_currentByte);
        while(true)
        {
            if (high < 0x8000)
				putBitWithPendingBits(0, l_pendingBits);
			else if (low >= 0x8000)
				putBitWithPendingBits(1, l_pendingBits);
			else if ((low >= 0x4000) && (high < 0xC000))
			{
				l_pendingBits++;
				low &= 0x3FFF;
				high |= 0x4000;
			}
			else break;
			low <<= 1;
			high <<= 1;
			high |= 1;
			high &= 0xFFFF;
			low &= 0xFFFF;
        }
    }
    l_pendingBits++;
    putBitWithPendingBits((low >> 14) & 1, l_pendingBits);
    m_compressedFileStream.forceWrite();
}

void Arithmetic::decodeStream()
{
    unsigned int value = 0;
    unsigned int low = 0;
    unsigned int high = 0xFFFF;
    unsigned long int l_outputFileSize = readResultFileSize();
    m_compressedFileStream.setFile(&m_inputFile);
    for (int i = 0; i<16; i++)
        value = (value << 1) | m_compressedFileStream.getBit();
    for (unsigned long int i = 0;i<l_outputFileSize; i++)
    {
        unsigned int range = high - low + 1;
        unsigned int l_projectedValue = ((value - low + 1) * m_partialSum.getSumOfAllElements() - 1) / range;
        unsigned char l_currentByte = m_partialSum.getCharacter(l_projectedValue);
        m_outputFile.write((char*)&l_currentByte, sizeof(char));
        unsigned int up = m_partialSum[l_currentByte+1];
        unsigned int down = m_partialSum[l_currentByte];
        high = low + (range * up) / m_partialSum.getSumOfAllElements() - 1;
        low = low + (range * down) / m_partialSum.getSumOfAllElements();
        m_partialSum.updatePartialSum(l_currentByte);
        while(true)
        {
			if (high < 0x8000 || low >= 0x8000);
			else if ((low >= 0x4000) && (high < 0xC000))
			{
				value ^= 0x4000;
				low &= 0x3FFF;
				high |= 0x4000;
			}
			else break;
			value <<= 1;
			value |= m_compressedFileStream.getBit();
			low <<= 1;
			high <<= 1;
			high |= 1;
			high &= 0xFFFF;
			low &= 0xFFFF;
            value &= 0xFFFF;
        }
    }
}

void Arithmetic::putBitWithPendingBits(unsigned char p_bit, unsigned int& p_pendingBits)
{
    m_compressedFileStream.putBit(p_bit);
    for(; p_pendingBits > 0; p_pendingBits--)
        m_compressedFileStream.putBit(1-p_bit);
}

unsigned long int Arithmetic::readResultFileSize()
{
    unsigned long int l_outputFileSize = 0;
    for (int i = 0; i < sizeof(unsigned long int); i++)
    {
        char byte = 0;
        m_inputFile.read(&byte, sizeof(char));
        l_outputFileSize = (l_outputFileSize << (sizeof(char)*8)) | byte;
    }
    return l_outputFileSize;
}

void Arithmetic::writeInputFileSize()
{
    unsigned long int l_size = m_inputFileSize;
    const unsigned int l_bytes = sizeof(unsigned long int);
    for (int i = 0; i<l_bytes; i++)
    {
        char byte = ((m_inputFileSize >> ((l_bytes-i-1)*(sizeof(char)*8))) & 0xFF );
        m_outputFile.write(&byte, sizeof(char));
    }
}
