#include <ICompress.hpp>
#include <BitStream.hpp>
#include <ExtendedString.hpp>


class LZW : public ICompress
{
public:
    LZW(const char* p_inputFilePath, const char* p_outputFilePath);

    void codeStream() override;
    void decodeStream() override;

private:
    void putFibCode(unsigned int number);
    void generateFibbonaciSeries(unsigned int fibbLen);
    int getFibCodeNum();

    BitStream m_bitstream;
    std::vector<unsigned int> fibb;
};
