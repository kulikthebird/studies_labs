#ifndef BACKENDINTERTRANSLATOR_HPP
#define BACKENDINTERTRANSLATOR_HPP

#include <Utils.hpp>

class InterToOutputTranslator
{
public:
    OutputCodeVector translateInterCodeToOutput(InterCodeVector &instructions);
};

#endif // BACKENDINTERTRANSLATOR_HPP
