import matplotlib.pyplot as plt
import numpy as np
from distributions import *


class Paging(object):
    def __init__(self, cacheSize):
        self.cache = [-1]*cacheSize
        self.index = 0

    def getPage(self, element):
        if element not in self.cache:
            self._pagingFault(element)
            return 1
        self._pagingSuccess(element)
        return 0

    def _pagingSuccess(self, element):
        pass

    def _pagingFault(self, element):
        raise "You need to override this method"

class Fifo(Paging):
    def _pagingFault(self, element):
        self.cache[self.index] = element
        self.index += 1
        self.index %= len(self.cache)

class Fwf(Paging):
    def _pagingFault(self, element):
        if self.index == len(self.cache):
            self.cache = [-1]*len(self.cache)
            self.index = 0
        self.cache[self.index] = element
        self.index += 1

class Lru(Paging):
    def _pagingSuccess(self, element):
        self.cache.remove(element)
        self.cache.insert(0, element)

    def _pagingFault(self, element):
        self.cache.pop(-1)
        self.cache.insert(0, element)

class Lfu(Paging):
    def __init__(self, n):
        Paging.__init__(self, n)
        self.freqList = {}

    def _pagingFault(self, element):
        if self.index == len(self.cache):
            freqs = [self.freqList[i] for i in self.cache]
            self.cache[freqs.index(np.min(freqs))] = element
        else:
            self.cache[self.index] = element
            self.index += 1
        if element in self.freqList:
            self.freqList[element] += 1 
        else:
            self.freqList[element] = 1

    def _pagingSuccess(self, element):
        if element in self.freqList:
            self.freqList[element] += 1 
        else:
            self.freqList[element] = 1

class Rand(Paging):
    def _pagingFault(self, element):
        if self.index == len(self.cache):
            self.cache[random.randint(0,len(self.cache)-1)] = element
        else:
            self.cache[self.index] = element
            self.index += 1

class Rma(Paging):
    def __init__(self, n):
        Paging.__init__(self, n)
        self.marks = [0]*n

    def _pagingFault(self, element):
        unmarked = [i for i in range(len(self.marks)) if self.marks[i] == 0]
        if len(unmarked) == 0:
            self.marks = [0]*len(self.marks)
            unmarked = range(len(self.marks))
        position = unmarked[random.randint(0, len(unmarked)-1)]
        self.marks[position] = 1
        self.cache[position] = element

def experiment(inputN, kList, randomFunction, algorithm, repeats):
    result = {}
    for n in inputN:
        result[n] = []
        for k in kList:
            repeatedExpResults = []
            for _ in range(repeats):
                cacheAlgorithm = algorithm(int(n*k))
                partialResult = 0
                for _ in range(n):
                    partialResult += cacheAlgorithm.getPage(randomFunction[n].next())
                repeatedExpResults += [partialResult]
            result[n] += [float(np.mean(repeatedExpResults)) / n]
    return result

def generate():
    result = {}
    inputN = [20, 40, 60, 80, 100]
    inputK = [1./10, 1./9, 1./8, 1./7, 1./6, 1./5]
    repeats = 100
    algorithms = [Fifo, Fwf, Lru, Lfu, Rand, Rma]
    distributions = [Uni, Har, Har2, Exp]
    randomFunctions = {}
    for dist in distributions:
        randomFunctions[dist] = {}
        for n in inputN:
            randomFunctions[dist][n] = dist(n)
    for dist in distributions:
        for alg in algorithms:
            result[(dist.__name__, alg.__name__)] = experiment(inputN, inputK, randomFunctions[dist], alg, repeats)
    fig, axs = plt.subplots(len(distributions), len(inputN))
    for n, j in zip(inputN, range(len(inputN))):
        for dist, i in zip(distributions, range(len(distributions))):
            legend = []
            for alg, color in zip(algorithms, ['r', 'g', 'y', 'b', 'm', 'k']):
                line, = axs[i][j].plot(inputK, result[(dist.__name__, alg.__name__)][n], linestyle='-', linewidth=2, color=color)
                legend += [line]
            axs[i][j].legend(legend, [alg.__name__ for alg in algorithms], prop={'size': 8}, loc=2)
    plt.show()


generate()
