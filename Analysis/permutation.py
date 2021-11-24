import numpy as np
import matplotlib.pyplot as plt
import random


def harmonic_number(n):
    gamma = 0.57721566490153286060651209008240243104215933593992
    return gamma + np.log(n) + 0.5/n - 1./(12*n**2) + 1./(120*n**4)

def countCycles(l):
    permutation = list(l)
    cycles = 0
    for i in range(len(permutation)):
        currentIndex = i
        if permutation[currentIndex] == -1:
            continue
        cycles += 1
        while permutation[currentIndex] != -1:
            newIndex = l[currentIndex]
            permutation[currentIndex] = -1
            currentIndex = newIndex
    return cycles

def countFixedPoints(l):
    fixed = 0
    for i in range(len(l)):
        if l[i] == i:
            fixed += 1
    return fixed

def countRecords(l):
    records = 0
    max = 0
    for elem in l:
        if elem > max:
            records += 1
            max = elem
    return records

def experiment(n, repeats):
    cycles = []
    fixedPoints = []
    records = []
    l = range(n)
    for i in range(repeats):
        random.shuffle(l)
        cycles += [countCycles(l)]
        fixedPoints += [countFixedPoints(l)]
        records += [countRecords(l)]
    return cycles, fixedPoints, records

def flatten(l):
    return [item for sublist in l for item in sublist]

def chernoffUp(x, expectedValueFunction, probab):
    mean = expectedValueFunction(x)
    return mean*(1. + np.sqrt(4*np.log(1./probab) / mean))

def chernoffDown(x, expectedValueFunction, probab):
    mean = expectedValueFunction(x)
    return mean*(1. - np.sqrt(2*np.log(1./probab) / mean))

def zadanie(lowerBound, upperBound, repeats):
    cycles = []
    fixedPoints = []
    records = []
    for n in range(lowerBound, upperBound):
        print("woring on n={}".format(n))
        result = experiment(n, repeats)
        cycles += [result[0]]
        fixedPoints += [result[1]]
        records += [result[2]]
    xAxis = np.arange(lowerBound, upperBound)
    fig, axs = plt.subplots(3)

    axs[0].plot(xAxis, cycles, linestyle=':', linewidth=3, color='g')
    axs[0].plot(range(lowerBound, upperBound), [np.mean(x) for x in cycles], linestyle=':', markersize=3, color='r')
    axs[0].plot(xAxis, chernoffUp(xAxis, harmonic_number, 0.10), linestyle='-', color='b', linewidth=3)
    axs[0].plot(xAxis, chernoffDown(xAxis, harmonic_number, 0.10), linestyle='-', color='b', linewidth=3)

    axs[1].plot(xAxis, fixedPoints, linestyle=':', linewidth=3, color='g')
    axs[1].plot(range(lowerBound, upperBound), [np.mean(x) for x in fixedPoints], linestyle=':', markersize=10, color='r')

    axs[2].plot(xAxis, records, linestyle=':', linewidth=3, color='g')
    axs[2].plot(xAxis, [np.mean(x) for x in records], linestyle=':', markersize=10, color='r')
    axs[2].plot(xAxis, chernoffUp(xAxis, harmonic_number, 0.10), linestyle='-', color='b', linewidth=3)
    axs[2].plot(xAxis, chernoffDown(xAxis, harmonic_number, 0.10), linestyle='-', color='b', linewidth=3)
    plt.show()

zadanie(1, 1000, 30)