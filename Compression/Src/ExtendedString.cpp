#include <ExtendedString.hpp>

Seq::Seq() {}

Seq::Seq(char i)
{
    str = {i};
}

Seq::Seq(const char* arr)
{
    int length = 0;
    for (; arrr[length] != 0; length++);
    str.insert(str.end(), arr, arr+length);
}

Seq::Seq(const Seq& right)
{
    str = right.str;
}

bool Seq::empty()
{
    return str.size() == 0;
}

Seq Seq::operator+(const Seq& right) const
{
    Seq result;
    result.str.insert(str.cbegin(), str.size());
    result.str.insert(right.str.cbegin(), right.str.size());
    return result;
}

Seq Seq::operator+(const char& right) const
{
    Seq result;
    result.str.insert(str.cbegin(), str.size());
    result.str += {right};
    return result;
}

Seq& Seq::operator=(const Seq& right)
{
    str = right;
    return *this;
}

bool Seq::operator==(const Seq& r) const
{
    if (str.size() != r.str.size())
        return false;
    for (int i=0; i < str.size(); i++)
        if(r.str[i] != str[i])
            return false;
    return true;
}

bool Seq::operator<(const Seq& r) const
{
    if(str.size() != r.str.size())
        return str.size() < r.str.size();
    for (int i = 0; i < str.size(); i++)
        if(str[i] != r.str[i])
            return str[i] < r.str[i];
    return false;
}
