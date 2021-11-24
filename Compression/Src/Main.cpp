#include <iostream>
#include <string>
#include <vector>
#include <algorithm>
#include <Huffman.hpp>
#include <Arithmetic.hpp>
#include <Lz77.hpp>
#include <Lzw.hpp>


std::string getParameter(std::string p_label, std::vector<std::string> p_argList)
{
    auto l_file = std::find(p_argList.cbegin(), p_argList.cend(), p_label);
    if(l_file != p_argList.end() && (l_file+1) != p_argList.end())
        return *(l_file+1);
    return "";
}

bool isParameterPresent(std::string p_label, std::vector<std::string> p_argList)
{
    return std::find(p_argList.begin(), p_argList.end(), p_label) != p_argList.end();
}

int main(int argc, char** argv)
{
    std::vector<std::string> argList;
    for (int i = 0; i < argc; i++)
        argList.push_back(argv[i]);
    if (argList.size() < 2)
    {
        std::cout << "Data compressing tool." << std::endl;
        return 0;
    }
    ICompress* l_compressAlgorithm;
    std::string l_outputFile;
    std::string l_inputFile;
    l_inputFile = getParameter("-i", argList);
    if(l_inputFile == "")
        return 0;
    l_outputFile = getParameter("-o", argList);
    bool l_isCompressionMode = !isParameterPresent("-d", argList);
    if(l_outputFile == "")
    {
        if(l_isCompressionMode)
            l_outputFile = l_inputFile + ".cmp";
        else if(!l_isCompressionMode && l_inputFile.size() > 4 && l_inputFile.substr(l_inputFile.size()-4, 4) == ".cmp")
            l_outputFile = l_inputFile.substr(0, l_inputFile.size() - 4);
    }
    std::string l_aParam = getParameter("-a", argList);
    if(l_aParam == "huffman")
        l_compressAlgorithm = new Huffman(l_inputFile.c_str(), l_outputFile.c_str());
    else if(l_aParam == "arithmetic")
        l_compressAlgorithm = new Arithmetic(l_inputFile.c_str(), l_outputFile.c_str());
    else if(l_aParam == "lz77")
        l_compressAlgorithm = new LZ77(l_inputFile.c_str(), l_outputFile.c_str());
    else if(l_aParam == "lzw")
        l_compressAlgorithm = new LZW(l_inputFile.c_str(), l_outputFile.c_str());
    else
    {
        // std::cout << "Selected default algorithm: huffman" << std::endl;
        // l_compressAlgorithm = new Huffman(l_inputFile.c_str(), l_outputFile.c_str());
        std::cout << "Selected default algorithm: LZ77" << std::endl;
        l_compressAlgorithm = new LZ77(l_inputFile.c_str(), l_outputFile.c_str());
    }
    std::cout << "In file: " << l_inputFile << " -> output file: " << l_outputFile << std::endl;
    if(l_isCompressionMode)
        l_compressAlgorithm->codeStream();
    else
        l_compressAlgorithm->decodeStream();
    delete l_compressAlgorithm;
    return 0;
}




//int main(int argc, char* argv[])
//{
//    std::vector<std::string> argList;
//    for (int i = 0; i < argc; i++)
//        argList.push_back(argv[i]);
//    if (argList.size() < 4)
//        std::cout << "Data compressing tool." << std::endl;
//    else if (argList.size() == 4)
//    {
//        Entropy ent1;
//        cout << "Entropy before compressing: " << ent1.showEntropy() << endl;
//        cout << "Conditional entropy (2 bytes) before compressing:  " << ent1.showCondEntropy() << endl;

//        if (argList[1] == "-c")
//        {
//            tre.setInputFile(argList[2]);
//            tre.setOutputBitFile(argList[3]);
//            cout << "Compressing: \n\n";
//            tre.codeStream();
//            cout << endl << "Done!\n\n";
//            tre.close();
//        }
//        else if (argList[1] == "-d")
//        {
//            tre.setInputBitFile(argList[2]);
//            tre.setOutputFile(argList[3]);
//            cout << "Decoding: \n\n";
//            tre.decodeStream();
//            cout << endl << "Done!\n\n";
//            tre.close();
//        }

//        Entropy ent2;
//        ent2.analyzeFile(argList[3].c_str());
//        ent2.countEntropy();
//        cout << "Entropy after compressing: " << ent2.showEntropy() << endl;
//        cout << "Conditional entropy (2 bytes) after compressing:  " << ent2.showCondEntropy() << endl;
//    }

//    return 0;
//}
