import matplotlib.pyplot as plt
import numpy as np
from distributions import *
import random


def generateSeries(randomFunction, size):
    result = []
    while len(result)<100:
        result += [random.random()]* min(randomFunction.next(), size - len(result))
    return result


class BinPacker(object):
    def __init__(self):
        self.bins = [0]
    
    def pack(self, x):
        raise "override this function"

    def getNumberOfBins(self):
        return len(self.bins)


class NextFit(BinPacker):
    def pack(self, x):
        if self.bins[-1] + x <= 1:
            self.bins[-1] += x
        else:
            self.bins += [x]

class RandomFit(BinPacker):
    def pack(self, x):
        random.shuffle(self.bins)
        for i in range(0, len(self.bins)):
            if self.bins[i] + x <= 1:
                self.bins[i] += x
                return
        self.bins += [x]

class FirstFit(BinPacker):
    def pack(self, x):
        for i in range(0, len(self.bins)):
            if self.bins[i] + x <= 1:
                self.bins[i] += x
                return
        self.bins += [x]

class BestFit(BinPacker):
    def pack(self, x):
        self.bins.sort(reverse=True)
        for i in range(0, len(self.bins)):
            if self.bins[i] + x <= 1:
                self.bins[i] += x
                return
        self.bins += [x]

class WorstFit(BinPacker):
    def pack(self, x):
        self.bins.sort()
        for i in range(0, len(self.bins)):
            if self.bins[i] + x <= 1:
                self.bins[i] += x
                return
        self.bins += [x]

class Optimal(BinPacker):
    def packSeries(self, series):
        pass


def generateResults():
    result = {}
    repeats = 1500
    p = 10
    algorithms = [NextFit, RandomFit, FirstFit, BestFit, WorstFit]
    distributions = [Uni(p), Har(p), Har2(p), Exp(p)]
    for d in distributions:
        result[d] = {}
        for _ in range(repeats):
            series = generateSeries(d, 100)
            sumOfSeries = np.sum(series)
            for a in algorithms:
                binPacker = a()
                for e in series:
                    binPacker.pack(e)
                if a in result[d]:
                    result[d][a] += [binPacker.getNumberOfBins() / float(sumOfSeries)]
                else:
                    result[d][a] = [binPacker.getNumberOfBins() / float(sumOfSeries)]
    fig, axs = plt.subplots(len(distributions)/2, len(distributions)/2)
    for d, i in zip(distributions, range(len(distributions))):
        legend = []
        for r, j in zip(result[d].values(), range(len(result[d].values()))):
            legend += axs[i%2][i/2].bar([j], np.max(r))
        for j, c in zip(range(len(legend)), ['r', 'g', 'y', 'b', 'm']):
            legend[j].set_color(c)
        axs[i%2][i/2].legend(legend, [alg.__name__ for alg in result[d].keys()], prop={'size': 7}, loc=2)
    plt.show()

generateResults()
