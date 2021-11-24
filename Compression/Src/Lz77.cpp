#include <Lz77.hpp>
#include <string>


LZ77::LZ77(const char* p_inputFilePath, const char* p_outputFilePath)
    :    ICompress(p_inputFilePath, p_outputFilePath), tab(len, 0)
{}

void LZ77::codeStream()
{
    int read_bytes;
    m_inputFile.read(&tab[mid], sizeof(char)* (mid + 1));
    read_bytes = m_inputFile.gcount();
    while (read_bytes != 0)
    {
        int temp_length = 0, temp_wsk = 0, max_length = 0, max_wsk = 0;
        for (int i = 0; i < mid; i++)
            if (tab[(begin + i) % len] == tab[(begin + mid) % len])
            {
                temp_length = 0;
                temp_wsk = i;
                for (int j = 0; j < read_bytes; j++)
                    if (tab[(begin + i + j) % len] == tab[(begin + mid + j) % len])
                        temp_length++;
                    else break;
                if (temp_length > max_length)
                {
                    max_length = temp_length;
                    max_wsk = temp_wsk;
                }
            }
        char out[2];
        if (max_length == 0)
        {
            max_length++;
            out[1] = tab[(begin + mid) % len];
            out[0] = 0;
        }
        else
        {
            out[0] = mid - max_wsk;
            out[1] = max_length - 1;
        }
        if (begin + max_length > len)
        {
            m_inputFile.read(&tab[(len + begin) % len], sizeof(char)* (len - begin));
            m_inputFile.read(&tab[0], sizeof(char)* (max_length - (len - begin)));
            read_bytes -= max_length - (m_inputFile.gcount() + m_inputFile.gcount());
        }
        else
        {
            m_inputFile.read(&tab[(len + begin) % len], sizeof(char)* (max_length));
            read_bytes -= max_length - m_inputFile.gcount();
        }
        begin += max_length;
        begin %= len;
        m_outputFile.write(out, sizeof(char)* 2);
    }
}

void LZ77::decodeStream()
{
    while (!m_inputFile.eof())
    {
        unsigned char in[2];
        m_inputFile.read((char*)in, sizeof(char)* 2);
        if (m_inputFile.eof())
            break;
        if (in[0] == 0)
        {
            tab[begin % len] = in[1];
            m_outputFile.write((char*)&in[1], sizeof(char));
            begin++;
            begin %= len;
        }
        else
        {
            for (int i = 0; i < in[1] + 1; i++)
            {
                char a = tab[(len + begin - in[0] + i) % len];
                tab[(begin + i) % len] = a;
                m_outputFile.write(&a, sizeof(char));
            }
            begin += in[1] + 1;
            begin %= len;
        }
    }
}
