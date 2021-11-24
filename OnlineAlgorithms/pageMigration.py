import matplotlib.pyplot as plt
import numpy as np
from distributions import *
import random
import networkx as nx
import sys


class Graph(object):
    def __init__(self, graph):
        self.__graph = graph
    
    def distance(self, node1, node2):
        return nx.shortest_path_length(self.__graph, source=node1, target=node2)

    def avgDistance(self, node, reqNodes):
        dist = 0
        for req in reqNodes:
            dist += self.distance(req, node)
        return float(dist) / len(reqNodes)

    def randomNode(self):
        return self.__graph._node.keys()[random.randint(0,self.__graph.number_of_nodes()-1)]

    def minimizeDistance(self, reqNodes):
        minNode = self.__graph._node.keys()[0]
        minDist = self.avgDistance(minNode, reqNodes)
        for node in self.__graph._node.keys()[1:]:
            d = self.avgDistance(node, reqNodes)
            if d < minDist:
                minDist = d
                minNode = node
        return minNode

    def getNodeById(self, nodeId):
        return self.__graph._node.keys()[nodeId]

    def getNumOfNodes(self):
        return self.__graph.number_of_nodes()


class PageMigration(object):
    def __init__(self, graph, startPageNode, cost):
        self.pageNode = startPageNode
        self.graph = graph
        self.cost = cost
    
    def movePage(self, newNode):
        cost = self.graph.distance(self.pageNode, newNode) * self.cost
        self.pageNode = newNode
        return cost

    def access(self, node):
        raise "override this function"


class MoveToMin(PageMigration):
    def __init__(self, graph, startPageNode, cost):
        PageMigration.__init__(self, graph, startPageNode, cost)
        self.__counter = 0
        self.__requests = []

    def access(self, node):
        cost = self.graph.distance(self.pageNode, self.graph.getNodeById(node))
        self.__requests += [self.graph.getNodeById(node)]
        self.__counter += 1
        if self.__counter == self.cost:
            self.__counter = 0
            cost += self.movePage(self.graph.minimizeDistance(self.__requests))
            self.__requests = []
        return cost


class RandomFlip(PageMigration):
    def __init__(self, graph, startPageNode, cost):
        PageMigration.__init__(self, graph, startPageNode, cost)

    def access(self, node):
        cost = self.graph.distance(self.pageNode, self.graph.getNodeById(node))
        if random.random() < 1./(2*self.cost):
            cost += self.movePage(self.graph.getNodeById(node))
        return cost


class OptimalOfflineAlgorithm(PageMigration):
    def __init__(self, graph, startPageNode, cost):
        PageMigration.__init__(self, graph, startPageNode, cost)
        self.lastCol = [sys.maxint/4]*graph.getNumOfNodes()
        self.lastCol[0] = 0

    def access(self, node):
        newLastCol = list(self.lastCol)
        for i in range(len(self.lastCol)):
            newLastCol[i] = np.min([self.lastCol[j] + 
                self.graph.distance(self.graph.getNodeById(j), self.graph.getNodeById(node)) + 
                self.cost * self.graph.distance(self.graph.getNodeById(j), self.graph.getNodeById(i))
                for j in range(len(self.lastCol))])
        self.lastCol = newLastCol
        return 0

    def getOptimal(self):
        print np.min(self.lastCol)
        return np.min(self.lastCol)



def generateSeries(randomFunction, size):
    return [randomFunction.next() for _ in xrange(size)]

def generateResults():
    D = 32
    repeats = 4
    nodesNumber = 64
    seriesSize = 1024
    algorithms = [MoveToMin, RandomFlip, OptimalOfflineAlgorithm]
    distributions = [Uni(nodesNumber), Har(nodesNumber), Har2(nodesNumber)]
    graphs = [Graph(nx.grid_graph(dim=[4,4,4], periodic=True)), Graph(nx.hypercube_graph(6))]

    resultDict = {}
    for d in distributions:
        resultDict[d] = {}
        for g in graphs:
            resultDict[d][g] = {}
            for _ in range(repeats):
                series = generateSeries(d, seriesSize)
                for a in algorithms:
                    algorithm = a(g, g.randomNode(), D)
                    cost = 0
                    for e in series:
                        cost += algorithm.access(e)
                    if a == OptimalOfflineAlgorithm:
                        cost = algorithm.getOptimal()
                    if a in resultDict[d]:
                        resultDict[d][g][a] += [cost]
                    else:
                        resultDict[d][g][a] = [cost]
                
    fig, axs = plt.subplots(3, 2)
    for graph, graphNum in zip(graphs, range(len(graphs))):
        for distribution, distNum in zip(distributions, range(len(distributions))):
            legend = []
            for result, resNum in zip(resultDict[distribution][graph].values(), range(len(resultDict[distribution][graph].values()))):
                legend += axs[distNum][graphNum].bar([resNum], np.mean(result))
            for num, color in zip(range(len(legend)), ['r', 'g', 'y', 'b', 'm']):
                legend[num].set_color(color)
            axs[distNum][graphNum].legend(legend, 
                [alg.__name__ for alg in resultDict[distribution][graph].keys()], prop={'size': 7}, loc=2)
    plt.savefig('page_migration.png')
    plt.show()

generateResults()
