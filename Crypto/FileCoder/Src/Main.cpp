#include <iostream>
#include <cstring>
#include <stdexcept>
#include <vector>
#include <openssl/rand.h>
#include <FileCipher.hpp>


#define BUFFER_SIZE     1024
enum OperationMode {NONE_MODE, ENCRYPTION_MODE, DECRYPTION_MODE, CHALLENGE_MODE};

struct UserInput
{
    OperationMode mode = NONE_MODE;
    AesOperationalMode cipherMode;
    std::string keystorePath;
    std::vector<std::string> inputFileNames;
    std::vector<std::string> outputFileNames;
};


void handleChoosenMode(const UserInput& userInput)
{
    std::string password;
    std::cout << "Type password: ";
    std::cin >> password;

    Cipher c(userInput.cipherMode);
    c.getKeyFromKeystore(userInput.keystorePath.c_str(), password.c_str());
    c.setBuffer(BUFFER_SIZE);
    switch(userInput.mode)
    {
        case ENCRYPTION_MODE:
        {
            for(unsigned int i=0; i<userInput.inputFileNames.size(); i++)
            {
                c.enableEncryptMode(userInput.inputFileNames[i].c_str(), userInput.outputFileNames[i].c_str());
                c.encryptFileToFile();
            }
        break;
        }
        case DECRYPTION_MODE:
        {
            for(unsigned int i=0; i<userInput.inputFileNames.size(); i++)
            {
                c.enableDecryptMode(userInput.inputFileNames[i].c_str(), userInput.outputFileNames[i].c_str());
                c.decryptFileToFile();
            }
        break;
        }
        case CHALLENGE_MODE:
        {
            unsigned char randomValue;
            RAND_bytes(&randomValue, 1);
            c.enableEncryptMode(userInput.inputFileNames[randomValue % 2].c_str(), userInput.outputFileNames[0].c_str());
            c.encryptFileToFile();
        break;
        }
        default:
            throw std::logic_error("You have to choose one option: -c | -d | -challenge");
        break;
    }
}

std::vector<std::string> readListOfArguments(const std::vector<std::string>& consoleInput, unsigned int position)
{
    std::vector<std::string> argsList;
    for(unsigned int i = position; i<consoleInput.size() && consoleInput[i][0] != '-'; i++)
        argsList.push_back(consoleInput[i]);
    return argsList;
}

UserInput handleConsoleArguments(const std::vector<std::string>& consoleInput)
{
    UserInput userInput;
    for(unsigned int i=1; i<consoleInput.size(); i++)
    {
        if(consoleInput[i] == "-e") {
            userInput.inputFileNames = readListOfArguments(consoleInput, i+1);
            userInput.mode = ENCRYPTION_MODE;
            i += userInput.inputFileNames.size();
        }
        else if(consoleInput[i] == "-d") {
            userInput.inputFileNames = readListOfArguments(consoleInput, i+1);
            userInput.mode = DECRYPTION_MODE;
            i += userInput.inputFileNames.size();
        }
        else if(consoleInput[i] == "--challenge") {
            if(i+2 >= consoleInput.size())
                throw std::logic_error("Too few file paths for challange mode.");
            userInput.inputFileNames.push_back(consoleInput[i+1]);
            userInput.inputFileNames.push_back(consoleInput[i+2]);
            userInput.mode = CHALLENGE_MODE;
            i += 2;
        }
        else if(consoleInput[i] == "-k") {
            userInput.keystorePath = consoleInput[i+1];
            i++;
        }
        else if(consoleInput[i] == "-o") {
            userInput.outputFileNames = readListOfArguments(consoleInput, i+1);
            i += userInput.outputFileNames.size();
        }
        else if(consoleInput[i] ==  "-m") {
            if(consoleInput[i+1] == "CRT")
                userInput.cipherMode = ENCRYPT_CRT;
            else if(consoleInput[i+1] == "OFB")
                userInput.cipherMode = ENCRYPT_OFB;
            else if(consoleInput[i+1] == "CBC")
                userInput.cipherMode = ENCRYPT_CBC;
            i++;
        }
        else if(consoleInput[i] ==  "-iv") {
            // TODO: get IV number
            i++;
        }
        else throw std::logic_error(std::string("Unknown parameter: ") + consoleInput[i]);
    }
    if(userInput.inputFileNames.size() == 0)
        throw std::logic_error("You have to choose input file.");
    for(unsigned int i=userInput.outputFileNames.size(); i<userInput.inputFileNames.size(); i++)
    {
        auto& inputFile = userInput.inputFileNames[i]
        if(userInput.mode == DECRYPTION_MODE)
        {
            if(inputFile.substr(inputFile.length-4, 4) == ".enc")
                userInput.outputFileNames.push_back(inputFile.substr(0, inputFile.length-4));
            else
                userInput.outputFileNames.push_back(inputFile + ".dec");
        }
        else
            userInput.outputFileNames.push_back(inputFile + ".enc");
    }
    return userInput;
}


int main(int argc, char** argv)
{
    if(argc < 3)
    {
        std::cout << "Program is used to encode/decode files using AES alghoritm.\n" \
                "-e [[list of file names]]\tEncrypt file.\n" \
                "-d [[list of file names]]\tDecrypt file.\n" \
                "-challenge [filename1] [filename2]\tChallenge mode.\n" \
                "-k [filename]\tKeystore file.\n" \
                "-m [mode]\tAES mode: CRT, OFB, CBC\n" \
                "-o [[list of file names]]\t(Optional) List of output file names - default inputname + .enc\n" \
                "-iv <hack> set IV";
        return 0;
    }
    std::vector<std::string> consoleInput;
    for(int i=0; i<argc; i++)
        consoleInput.push_back(argv[i]);
    try
    {
        auto userInput = handleConsoleArguments(consoleInput);
        handleChoosenMode(userInput);
    }
    catch(std::exception& e)
    {
        std::cout << e.what() << std::endl;
        return 1;
    }
	return 0;
}
