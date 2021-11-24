#include <iostream>
#include <fstream>

class Key
{
public:
    int length;
    unsigned char* key;
    Key& operator=(const Key& other) // copy assignment
    {
        if (this != &other) { // self-assignment check expected
            if (other.length != length) {         // storage cannot be reused
                delete[] key;              // destroy storage in this
                key = new unsigned char[other.length]; // create storage in this
                length = other.length;
            }
            std::copy(other.key, other.key + other.length, key);
        }
        return *this;
    }
    unsigned int getBit(unsigned int i)
    {
        return (key[i/8] >> i%8) & 0x1;
    }
};

class RC4
{
    public:
        RC4(int N, int T, Key key) : N(N), T(T), key(key), i(0), j(0)
        {
            initKsaK();
        }

        unsigned char prgasNextByte()
        {
            i = (i+1) % N;
            j = (j + S[i]) % N;
            unsigned char temp = S[j];
            S[j] = S[i];
            S[i] = temp;
            return S[(S[i] + S[j]) % N];
        }

    private:
        void initKsaK()
        {
            S = new unsigned char[N];
            for(int i=0; i<N; i++)
                S[i]=i;
            j = 0;
            for(int i=0; i < T; i++)
            {
                j = (j + S[i % N] + key.key[i % key.length]) % N;
                unsigned char temp = S[j%N];
                S[j%N] = S[i%N];
                S[i%N] = temp;
            }
        }

//        void initKsaRs()
//        {
//            for(int i=0; i<N; i++)
//                S[i] = i;
//            for(int r=0; r<T; r++)
//            {
//                vector<unsigned int> Top;
//                vector<unsigned int> Bottom;
//                for(int i=0; i<=N; i++)   // Tutaj uwaga na ograniczenie górne
//                {
//                    if(key.getBit((r*N + i) % (key.length*8)) == 0)
//                        Top.push(i);
//                    else
//                        Bottom.push(i)
//                }
//                for(int i=0; i<Top.size(); i++)
//                    S[i] = Top[i];
//                for(int i=0; i<Bottom.size(); i++)
//                    S[Top.size() + i] = Bottom[i];
//            }
//        }

        unsigned int N;
        unsigned int T;
        unsigned char* S;
        unsigned int i, j;
        Key key;
};


int main(int argc, char** argv)
{
    if(argc < 4) return 1;
    Key k;
    int n = std::stoi(argv[1]);
    k.key = new unsigned char[std::stoi(argv[2])];
    k.length = std::stoi(argv[2]);
    for(int i=0; i<k.length; i++)
        k.key[i] = i;
    std::fstream f;
    f.open(argv[3], std::ios::out | std::ios::binary);
    unsigned char* buffer = new unsigned char[1024*1024];

    int method = std::stoi(argv[4]);
    RC4 *r;
    switch(method)
    {
        case 0:
            r = new RC4(n, n, k);
            break;
        case 1:
            r = new RC4(n, n, k);
            for(int i=0; i<n*k.length; i++)
                r->prgasNextByte();
            break;
        case 2:
            r = new RC4(n, n*k.length, k);
            break;
    }
    for(unsigned int i=0; i<1024; i++)
    {
        for(unsigned int j=0; j<1024*1024; j++)
        {
            buffer[j] = r->prgasNextByte();
        }
        f.write((const char*)buffer, 1024*1024);
    }

    f.close();
    delete [] buffer;
    return 0;
}