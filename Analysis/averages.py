import random
import numpy as np
import matplotlib.pyplot as plt
from functools import reduce


def mean(iterable):
    return np.mean(iterable)

def gmean(iterable):
    a = np.array(iterable)
    return a.prod() ** (1.0 / len(a))

def hmean(iterable):
    it = np.array(iterable)
    return len(it) / reduce(lambda x,y: x+(1./y), it, 0)

def experiment(n, hugeDevPerc):
    resMean = []
    resGMean = []
    resHMean = []
    tab = [1000] * n
    for i in range(n):
        tab[i] = (1.+hugeDevPerc) * 1000
        resMean += [mean(tab)]
        resGMean += [gmean(tab)]
        resHMean += [hmean(tab)]
    fig, axs = plt.subplots(1)
    axs.plot(range(n), resMean, linestyle='-', linewidth=3, color='g')
    axs.plot(range(n), resGMean, linestyle='-', linewidth=3, color='b')
    axs.plot(range(n), resHMean, linestyle='-', linewidth=3, color='r')
    plt.show()

experiment(50, 1.5)
