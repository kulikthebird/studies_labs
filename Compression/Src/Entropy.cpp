#include <cmath>
#include <fstream>
#include <exception>
#include <vector>
#include <list>



typedef  std::vector<unsigned long long>    OneCharFrequency;
typedef  std::vector<OneCharFrequency>    TwoCharsFrequency;


class Entropy
{
public:
    Entropy(const char* p_path)
    {
        m_file.open(p_path, std::ios::in | std::ios::binary);
    }
    ~Entropy()
    {
        m_file.close();
    }

protected:
    double entropyElement(double sumOfChars, double m_fileSize);
    double countConditionalEntropy(OneCharFrequency p_fisrtCharFreq, TwoCharsFrequency p_secondCharFreqs, unsigned int p_fileSize);
    double countEntropy(OneCharFrequency p_oneCharsFreq, unsigned int m_fileSize);

    std::ifstream m_file;
    long long m_fileSize = 0;
};

double Entropy::entropyElement(double sumOfChars, double m_fileSize)
{
    if (sumOfChars > 0)
        return (sumOfChars / m_fileSize) * log2(m_fileSize / sumOfChars);
    return 0;
}

double Entropy::countConditionalEntropy(OneCharFrequency p_fisrtCharFreq, TwoCharsFrequency p_secondCharFreqs, unsigned int p_fileSize)
{
    double l_conditionalEntropy = 0;
    for(unsigned int i=0; i<p_fisrtCharFreq.size(); i++)
    {
        double x = (double)p_fisrtCharFreq.charFreq[i] / p_fileSize;
        double y = countEntropy(p_secondCharFreqs[i], p_fileSize);
        l_conditionalEntropy += x*y;
    }
    return l_conditionalEntropy;
}

double Entropy::countEntropy(OneCharFrequency p_oneCharsFreq, unsigned int m_fileSize)
{
    double l_entropy = 0;
    for(auto& freq : p_oneCharsFreq)
        l_entropy += entropyElement((double)freq, (double)m_fileSize);
    return l_entropy;
}


class StandardEntropy : public Entropy
{
public:
    StandardEntropy(const char* p_path) : Entropy(p_path) {}
    double count();

private:
    void countChars();
    OneCharFrequency m_oneCharFreq = std::vector(256, 0);
};

double StandardEntropy::count()
{
    countChars();
    return countEntropy(m_oneCharFreq, m_fileSize);
}

void StandardEntropy::countChars()
{
    unsigned char l_byte;
    while (m_file.read((char*)&byte, sizeof(char)))
    {
        m_fileSize++;
        m_oneCharFreq[l_byte]++;
    }
}


class ConditionalEntropy
{
public:
    ConditionalEntropy(const char* p_path) : Entropy(p_path) {}
    double count();

private:
    void countPairsOfChars();

    OneCharFrequency m_oneCharFreq = std::vector(256, 0);
    TwoCharsFrequency m_twoCharFreq = std::vector(256, OneCharFrequency(256, 0));
};

double ConditionalEntropy::count()
{
    countPairsOfChars();
    return countConditionalEntropy(m_oneCharFreq, m_twoCharFreq, m_fileSize);
}

void ConditionalEntropy::countPairsOfChars()
{
    unsigned char l_byte, l_prelastByte = 0;
    while (m_file.read((char*)&byte, sizeof(char)))
    {
        m_fileSize++;
        m_oneCharFreq[l_byte]++;
        m_twoCharFreq[l_prelastByte][l_byte]++;
        l_prelastByte = l_byte;
    }
}
