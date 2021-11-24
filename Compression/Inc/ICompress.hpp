#pragma once

#include <fstream>
#include <iostream>


class ICompress
{
public:
    ICompress() {}
    ICompress(const char* p_inputFilePath, const char* p_outputFilePath)
    {
        setInputFile(p_inputFilePath);
        setOutputFile(p_outputFilePath);
    }
    virtual ~ICompress() { close(); }

    virtual void codeStream() = 0;
    virtual void decodeStream() = 0;

    void close();
    void setInputFile(const char* p_path);
    void setOutputFile(const char* p_path);

protected:
    unsigned int getFileSize(const char* p_path);
    void updateProgressBar();

    unsigned long int m_inputFileSize;
    std::fstream m_inputFile;
    std::fstream m_outputFile;
    unsigned int m_currentPercent;
};
