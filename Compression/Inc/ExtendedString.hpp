#pragma once

#include <vector>


class Seq
{
public:
    Seq();
    Seq(char i);
    Seq(const char* arr);
    Seq(const Seq& right);
    bool empty();

    Seq operator+(const Seq& right) const;
    Seq operator+(const char& right) const;
    Seq& operator=(const Seq& right);
    bool operator==(const Seq& r) const;
    bool operator<(const Seq& r) const;

private:
    std::vector<char> str;
};
