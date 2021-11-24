import matplotlib.pyplot as plt
import numpy as np
from distributions import *


class ListBase(object):
    def __init__(self):
        self.inList = []

    def getPosition(self, element):
        for i in range(len(self.inList)):
            if self.inList[i] == element:
                return i
        return len(self.inList)

    def access(element):
        raise "You need to override this method"


class SimpleList(ListBase):
    def access(self, element):
        pos = self.getPosition(element)
        if pos == len(self.inList):
            self.inList += [element]
        return pos

class MoveToFrontList(ListBase):
    def access(self, element):
        pos = self.getPosition(element)
        if pos != len(self.inList):
            self.inList.pop(pos)
        self.inList.insert(0, element)
        return pos

class TransposeList(ListBase):
    def access(self, element):
        pos = self.getPosition(element)
        if pos == len(self.inList):
            self.inList += [element]
        elif pos != 0:
             self.inList[pos-1], self.inList[pos] = self.inList[pos], self.inList[pos-1]
        return pos

class CountList(ListBase):
    def __init__(self):
        ListBase.__init__(self)
        self.freqList = []

    def access(self, element):
        pos = self.getPosition(element)
        if pos == len(self.inList):
            self.inList += [element]
            self.freqList += [1]
        else:
            self.freqList[pos] += 1
            index = pos
            while index > 0 and self.freqList[index] < self.freqList[pos]:
                index -= 1
            self.inList[index], self.inList[pos] = self.inList[pos], self.inList[index]
            self.freqList[index], self.freqList[pos] = self.freqList[pos], self.freqList[index]
        return pos

def experiment(expList, randomFunction, data, repeats):
    resultDict = []
    for n in data:
        repeatResult = []
        for _ in range(repeats):
            result = []
            l = expList()
            for _ in range(n):
                result += [l.access(randomFunction.next())]
            repeatResult += [np.mean(result)]
        resultDict += [np.mean(repeatResult)]
    return resultDict

def test(data, distributions, lists, repeats):
    result = {}
    for dist in distributions:
        result[dist] = {}
        for l in lists:
            result[dist][l] = experiment(l, dist, data, repeats)
    return result

data = [100, 500, 1000, 5000, 10000, 50000, 100000]
distributions = [Uni(100), Har(100), Exp(100)]
lists = [SimpleList, MoveToFrontList, TransposeList, CountList]
result = test(data, distributions, lists, 20)

fig, axs = plt.subplots(3)
for i in range(3):
    axs[i].plot(data, result[distributions[i]][SimpleList], linestyle='-', linewidth=3, color='r')
    axs[i].plot(data, result[distributions[i]][MoveToFrontList], linestyle='-', linewidth=3, color='g')
    axs[i].plot(data, result[distributions[i]][TransposeList], linestyle='-', linewidth=3, color='b')
    axs[i].plot(data, result[distributions[i]][CountList], linestyle='-', linewidth=3, color='y')
plt.show()
