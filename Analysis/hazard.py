import random
import matplotlib.pyplot as plt


def game(win, lose, tries):
    sum = 0
    for i in range(tries):
        if random.randint(0,1) == 0:
            sum += win
        else:
            sum += lose
    return sum

def countProfitability(resultIterator):
    data = [res for res in resultIterator]
    fig1, axs1 = plt.subplots(1)
    axs1.hist(data, 5000, normed=1, facecolor='green', alpha=0.75)
    axs1.grid(True)

def experimentGenerator(win, lose, tries, experimentTries):
    for i in range(experimentTries):
        yield game(win, lose, tries)

experimentTries = 30000
theoreticalMean = (550*0.05 - 550*0.5)
res1 = countProfitability(experimentGenerator(0.05, -0.5, 1100, experimentTries))
res2 = countProfitability(experimentGenerator(11, -110, 5, experimentTries))
plt.show()