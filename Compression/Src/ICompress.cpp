#include <fstream>
#include <iostream>
#include <ICompress.hpp>


void ICompress::close()
{
    m_inputFile.close();
    m_outputFile.close();
}

void ICompress::setInputFile(const char* p_path)
{
    m_inputFileSize = getFileSize(p_path);
    m_inputFile.open(p_path, std::ios::in | std::ios::binary);
    if(m_inputFile.fail())
        std::cout << "Unable to open file " << p_path << std::endl;
}

void ICompress::setOutputFile(const char* p_path)
{
    m_outputFile.open(p_path, std::ios::out | std::ios::trunc | std::ios::binary);
    if(m_outputFile.fail())
        std::cout << "Unable to create file " << p_path << std::endl;
}

unsigned int ICompress::getFileSize(const char* p_path)
{
    std::ifstream l_file(p_path, std::ifstream::ate | std::ifstream::binary);
    if(!l_file.is_open())
        return 0;
    unsigned int l_result = l_file.tellg();
    l_file.close();
    return l_result;
}

void ICompress::updateProgressBar()
{
    unsigned int l_tempPercent = m_inputFile.tellg()*100 / m_inputFileSize;
    if (m_currentPercent < l_tempPercent)
    {
        m_currentPercent = l_tempPercent;
        std::cout << "\r" << m_currentPercent;
    }
}
