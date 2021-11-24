import random
import math
import matplotlib.pyplot as plt
import numpy

random.seed()
plt.figure(num=None, figsize=(14, 8), dpi=80, facecolor='w', edgecolor='k')


def election_with_n(n):
    single = False
    counter = 0
    while single != True:
        counter += 1
        single = False
        for i in range(0, n):
            r = random.randint(0, n)
            if r == 0:
                if single == False:
                    single = True
                else:
                    single = False
                    break
    return counter

def election_with_u(u, n):
    R = int(math.ceil(math.log(u, 2)))
    counter = 0
    single = False
    while not single:
        delta_u = 2
        for j in range(0, R):
            single = False
            counter += 1
            for i in range(0, n):
                if random.randint(0, delta_u) == 0:
                    if not single:
                        single = True
                    else:
                        single = False
                        break
            if single:
                break
            delta_u = delta_u * 2
    return counter

def plot_histogram(result, fig=1, subfig=211):
    res_maximum = max([max(x) for x in result])
    res_length = max([len(x) for x in result])
    plt.figure(fig)
    plt.subplot(subfig)
    bins = numpy.linspace(1, res_maximum, res_maximum)
    plt.hist(result, bins=bins)
    plt.axis([1, res_maximum, 0, res_length])
    plt.grid(True)

def plot_histogram_lambda(result, fig=2, subfig=211):
    res_length = max([len(x) for x in result])
    plt.figure(fig)
    plt.subplot(subfig)
    bins = numpy.linspace(0, 1, 100)
    plt.hist(result, bins=bins)
    plt.axis([0, 1, 0, numpy.mean(res_length)])
    plt.grid(True)

def sim_elect_with_n(n, tries):
    result = []
    for i in range(tries):
        result.append(election_with_n(n))
    return result

def sim_elect_with_u(u, n, tries):
    result = []
    for i in range(tries):
        result.append(election_with_u(u, n))
    return result

def count_lambda(a, u):
    return float(reduce(lambda x, y: x+1 if y <= int(math.ceil(math.log(u, 2))) else x, a, 0)) / len(a)

def zad1a():
    print 'Zadanie 1a'
    tries = 10000
    result = sim_elect_with_n(120, tries)
    plot_histogram([result], 1, 211)
    print 'n=20:  E[X]={}  Var[X]={}'.format(numpy.mean(result), numpy.var(result))

def zad1b():
    print 'Zadanie 1b'
    plt.suptitle('Lista I - zadanie 1')
    tries = 10000
    result1 = sim_elect_with_u(u=100, n=2, tries=tries)
    result2 = sim_elect_with_u(u=100, n=50, tries=tries)
    result3 = sim_elect_with_u(u=100, n=100, tries=tries)
    plot_histogram([result1, result2, result3], 1, 212)
    print 'u=20, n=2:  E[X]={}  Var[X]={}'.format(numpy.mean(result1), numpy.var(result1))
    print 'u=20, n=10:  E[X]={}  Var[X]={}'.format(numpy.mean(result2), numpy.var(result2))
    print 'u=20, n=20:  E[X]={}  Var[X]={}'.format(numpy.mean(result3), numpy.var(result3))

def zad2():
    print 'Zadanie 2'
    plt.suptitle('Lista I - zadanie 2')
    result = []
    u = 50
    for i in range(1):
        for j in range(2, u+1):
            n = j
            result.append(count_lambda(sim_elect_with_u(u=u, n=n, tries=3000), u))
    plot_histogram_lambda([result], 2, 211)
    print 'E[lambda]={}'.format(numpy.mean(result))
    print 'min[lambda]={}'.format(numpy.min(result))

zad1a()
zad1b()
zad2()
plt.show()
