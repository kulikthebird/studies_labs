import networkx as nx
import numpy as np
import matplotlib.pyplot as plt
import math


def experiment(n, p):
    G = nx.fast_gnp_random_graph(n, p)
    components = sorted(nx.connected_components(G), key = len, reverse=True)
    return len(components[0]) if len(components) > 0 else 0, len(components[1]) if len(components) > 1 else 0

def repeatExperiment(n, p, repeats):
    firstComponent = []
    secondComponent = []
    for i in range(repeats):
        first, second = experiment(n,p)
        firstComponent += [first]
        secondComponent += [second]
    return firstComponent, secondComponent

flatten = lambda l: [item for sublist in l for item in sublist]
params = [lambda n: 1./(2*n),
        lambda n: 1./n + math.pow(n, 0.1)/math.pow(n, 4./3),
        lambda n: 1./n - math.pow(n, 0.1)/math.pow(n, 4./3),
        lambda n: 1./n + 2/(math.pow(n, 4./3)),
        lambda n: 1./n - 2/(math.pow(n, 4./3)),
        lambda n: 2./n]

def plotResults(result1, result2, czebyszew11, czebyszew21, czebyszew12, czebyszew22, index, xAxis, xAxis2, plt):
    fig1, axs1 = plt.subplots(2)
    axs1[0].plot(xAxis, flatten(result1[params[index]]), linestyle=':', markersize=10, color='r')
    axs1[0].plot(xAxis2, czebyszew11[params[index]], linestyle=':', markersize=10, color='b')
    axs1[0].plot(xAxis2, czebyszew12[params[index]], linestyle=':', markersize=10, color='b')
    axs1[1].plot(xAxis, flatten(result2[params[index]]), linestyle=':', markersize=10, color='g')
    axs1[1].plot(xAxis2, czebyszew21[params[index]], linestyle=':', markersize=10, color='b')
    axs1[1].plot(xAxis2, czebyszew22[params[index]], linestyle=':', markersize=10, color='b')
    fig1.suptitle("Wykres {}".format(index))
    fig1.savefig("result{}.jpg".format(index))

def zadanie(lowerBound, upperBound, repeats):
    result1 = {}
    result2 = {}
    czebyszew11 = {}
    czebyszew12 = {}
    czebyszew21 = {}
    czebyszew22 = {}
    step = 100
    for param in params:
        result1[param] = []
        result2[param] = []
        czebyszew11[param] = []
        czebyszew21[param] = []
        czebyszew12[param] = []
        czebyszew22[param] = []
    for n in range(lowerBound, upperBound, step):
        print("working on n={}.".format(n))
        for param in params:
            first, second = repeatExperiment(n, param(n), repeats)
            result1[param] += [first]
            result2[param] += [second]
            czebyszew11[param] += [np.mean(first) - 2*math.sqrt(np.var(first))]
            czebyszew21[param] += [np.mean(second) - 2*math.sqrt(np.var(second))]
            czebyszew12[param] += [np.mean(first) + 2*math.sqrt(np.var(first))]
            czebyszew22[param] += [np.mean(second) + 2*math.sqrt(np.var(second))]
    xAxis = flatten([[x]*repeats for x in range(lowerBound, upperBound, step)])
    # xAxis = range(lowerBound, upperBound, 100)
    for i in range(len(params)):
        plotResults(result1, result2, czebyszew11, czebyszew21, czebyszew12, czebyszew22, i, xAxis, range(lowerBound, upperBound, step), plt)
    plt.show()

zadanie(400, 6000, 20)