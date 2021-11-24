#include <ICompress.hpp>
#include <vector>


class LZ77 : public ICompress
{
public:
    LZ77(const char* p_inputFilePath, const char* p_outputFilePath);

    void codeStream() override;
    void decodeStream() override;

private:
    unsigned int len = 511;
    unsigned int mid = 255;
    int begin = 0;
    std::vector<char> tab;
};
