#include <iostream>
#include <string.h>

using namespace std;

int** states;
int sigma_len = 'z' - 'a';

int delta(int q, char a)
{
    int _a = (int) a;
    return states[q][_a];
}

int finite_automaton_matcher(string text, int (*transition_function)(int, char), int m)
{
    int n = text.length();
    int q = 0;

    for(int i=0; i<n; i++)
    {
        q = transition_function(q, text[i] - 'a');
        if(q == m)
        {
            cout << "Wzorzec wystepuje z przesunieciem " << i - m + 1 << endl;
        }
    }
    return 0;
}

int compute_transition_function(string pattern)
{
    int m = pattern.length();
    states = new int* [m + 1];
    for(int i=0; i<=m; i++)
        states[i] = new int[sigma_len];
    for(int q = 0; q <= m; q++)
    {
        for(char a='a'; a <= 'z'; a++)
        {
            int k = min(q + 2, m + 1);
            string Pq = pattern.substr(0, q) + a;
            for(; k >= 0; k--)
            {
                if(k > q + 1)
                    continue;
                if(pattern.substr(0, k) == Pq.substr(Pq.length() - k, k))
                    break;
            }
            states[q][(int)(a - 'a')] = k;
        }
    }
    return 0;
}

int main(int argc, char** argv)
{
    string pattern = "abab";
    compute_transition_function(pattern);
    finite_automaton_matcher("abababba", &delta, pattern.length());
    return 0;
}
