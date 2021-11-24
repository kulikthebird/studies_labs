#include <vector>
#include <numeric>
#include <BitStream.hpp>
#include <ICompress.hpp>
#include <PartialSum.hpp>


class Arithmetic : public ICompress
{
public:
    const unsigned int m_numberOfCodeElements = 256;
    Arithmetic(const char* p_inputFilePath, const char* p_outputFilePath);

    void codeStream() override;
    void decodeStream() override;

private:
    void putBitWithPendingBits(unsigned char p_bit, unsigned int& p_pendingBits);
    unsigned long int readResultFileSize();
    void writeInputFileSize();

    PartialSum m_partialSum;
    BitStream m_compressedFileStream;
};
