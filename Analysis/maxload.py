import numpy as np
import matplotlib.pyplot as plt
import random


def chebyshevUp(mean, variance, probab):
    return mean + np.sqrt(variance) / np.sqrt(probab)

def chebyshevDown(mean, variance, probab):
    return mean - np.sqrt(variance) / np.sqrt(probab)

def getMinBinIndex(bins, binsList):
    minElement = bins[binsList[0]]
    index = binsList[0]
    for x in binsList:
        if bins[x] < minElement:
            index = x
            minElement = bins[x]
    return index

def randomized(n):
    table = [0]*n
    for x in range(n):
        table[random.randint(0, n-1)] += 1
    return np.max(table)

def dRandomElements(n, d):
    table = [0]*n
    for x in range(d, n):
        subset = []
        for i in range(d):
            r = random.randint(0, n-1)
            while r in subset:
                r = random.randint(0, n-1)
            subset += [r]
        table[getMinBinIndex(table, subset)] += 1
    return np.max(table)

def dSubsets(n, d):
    table = [0] * n
    for x in range(d, n):
        subset = [n*x/d + random.randint(0, np.ceil(n/d - 1.1)) for x in range(d)]
        table[getMinBinIndex(table, subset)] += 1
    return np.max(table)

def flatten(l):
    return [item for sublist in l for item in sublist]

def experiment(maxN, experiment, repeat, expectedValueFunction, **kwargs):
    xAxis = xrange(10, maxN+1, 9)
    result = [[experiment(n, **kwargs) for x in range(repeat)] for n in xAxis]
    fig, axs = plt.subplots(1)
    parameters = ', '.join(["{} = {}".format(key, val) for key, val in kwargs.iteritems()])
    fig.suptitle("Wykres {}, parametry maxN = {}. {}".format(experiment.func_name, maxN, parameters))
    axs.plot(flatten([[x]*repeat for x in xAxis]), flatten(result), linestyle='-', linewidth=1, color='g')
    axs.plot(xAxis, [chebyshevDown(np.mean(x), np.var(x), 0.10) for x in result], linestyle='-', color='b', linewidth=1)
    axs.plot(xAxis, [chebyshevUp(np.mean(x), np.var(x), 0.10) for x in result], linestyle='-', color='b', linewidth=1)
    fig.savefig("results/maxload{}_{}_{}.jpg".format(experiment.func_name, maxN, parameters))

repeats = 70
n = 5000
print("Licze 1 | d=2")
experiment(n, randomized, repeats, lambda x: np.log(x)/np.log(np.log(x))+2.0)
print("Licze 2 | d=2")
experiment(n, dRandomElements, repeats, lambda x: np.log(np.log(x)/np.log(d)) + 1.0, d=2)
print("Licze 3 | d=2")
experiment(n, dSubsets, repeats, lambda x: np.log(np.log(x)/np.log(d)) + 0.6, d=2)
print("Licze 4 | d=4")
experiment(n, dRandomElements, repeats, lambda x: np.log(np.log(x)/np.log(d)) + 1.0, d=4)
print("Licze 5 | d=4")
experiment(n, dSubsets, repeats, lambda x: np.log(np.log(x)/np.log(d)) + 0.6, d=4)
plt.show()
