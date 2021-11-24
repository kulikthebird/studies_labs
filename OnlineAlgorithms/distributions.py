import random
import numpy as np


def harmonic_number(n):
    gamma = 0.57721566490153286060651209008240243104215933593992
    return gamma + np.log(n) + 0.5/n - 1./(12*n**2) + 1./(120*n**4)

def harmonic_number2(n):
    result = 0
    for i in range(1, n+1):
        result += 1./(i*i)
    return result

class Exp(object):
    def __init__(self, n):
        pass

    def next(self):
        return np.random.geometric(0.5)

class Har(object):
    def __init__(self, n):
        self.tab = []
        last = 0
        harN = harmonic_number(n)
        for i in range(1, n + 1):
            last += 1./(harN*i)
            self.tab += [last]

    def next(self):
        r = random.random()
        for i in range(len(self.tab)):
            if r <= self.tab[i]:
                return i

class Har2(object):
    def __init__(self, n):
        self.tab = []
        last = 0
        har2N = harmonic_number2(n)
        for i in range(1, n + 1):
            last += 1./(har2N*i*i)
            self.tab += [last]

    def next(self):
        r = random.random()
        for i in range(len(self.tab)):
            if r <= self.tab[i]:
                return i

class Uni(object):
    def __init__(self, n):
        self.value = n

    def next(self):
        return int(random.random()*self.value)
