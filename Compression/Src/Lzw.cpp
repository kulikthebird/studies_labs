#include <Lzw.hpp>
#include <BitStream.hpp>
#include <ExtendedString.hpp>
#include <iostream>


LZW::LZW(const char* p_inputFilePath, const char* p_outputFilePath)
    :    ICompress(p_inputFilePath, p_outputFilePath)
{
    generateFibbonaciSeries(50);
}

void LZW::generateFibbonaciSeries(unsigned int fibbLen)
{
    int a = 0, b = 1;
    fibb = std::vector<unsigned int>(fibbLen);
    for (int i = 0; i <= fibbLen; i++)
    {
        a = a + b;
        a = a + b;
        b = a - b;
        a = a - b;
        fibb[i] = b;
    }
}

int LZW::getFibCodeNum()
{
    int result = 0;
    char prev = 0, bit = 0;
    bitstr.get(bit);
    for (int i = 0; !(prev & bit); i++)
    {
        if (bit)
            result += fibb[i];
        prev = bit;
        bitstr.get(bit);
        if (bitstr.eof())
            return -1;
    }
    return result;
}

void LZW::putFibCode(unsigned int number)
{
    int i = 0, out = 0;
    out |= 1;
    for (i = fib_len - 1; number < fibb[i]; i--);
    for (; i >= 0; i--)
    {
        if (number >= fibb[i])
        {
            out <<= 1;
            out |= 1;
            number -= fibb[i];
        }
        else
        {
            out <<= 1;
            out |= 0;
        }
    }
    int test = 0;
    while(out)
    {
        bitstr.put(out & 1);
        test << 1;
        test = out & 1;
        out >>= 1;
    }
}

void LZW::codeStream()
{
    map<Seq, int> dictionary;
    char c;
    int dictSize = 256;
    for (int i = 0; i < 256; i++)
        dictionary[Seq(i)] = i;
    file.read(&c, sizeof(char));
    Seq w;
    while (file.gcount() != 0)
    {
        Seq wc = w + c;
        if (dictionary.count(wc))
            w = wc;
        else {
            putFibCode(dictionary[w] + 1);
            dictionary[wc] = dictSize++;
            w = Seq(c);
        }
        file.read(&c, sizeof(char));
    }
    if (!w.empty())
        putFibCode(dictionary[w] + 1);
    bitstr.forceWrite();
}

void LZW::decodeStream()
{
    map<int, Seq> dictionary;
    int pk;
    int dictSize = 256;
    for (int i = 0; i < 256; i++)
        dictionary[i] = Seq(i);
    Seq w(getFibCodeNum() - 1);
    file.write(w.str, w.length);
    Seq entry;
    while (!bitstr.eof())
    {
        int k = getFibCodeNum() - 1;
        if (k == -2)
            break;
        if (dictionary.count(k))
            entry = dictionary[k];
        else if (k == dictSize)
            entry = w + w.str[0];
        else
            std::cout << "Bad compressed k";
        file.write(entry.str, entry.length);
        dictionary[dictSize++] = w + entry.str[0];
        w = entry;
    }
}
