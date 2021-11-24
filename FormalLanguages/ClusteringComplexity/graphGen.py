import networkx as nx
import matplotlib.pyplot as plt


def generateClique(nodes):
    return [(i, j)for i in nodes for j in nodes]

def draw_graph(nodes, edges, labels):
    plt.subplots()
    plt.axis('off')
    colors = \
        {
            r'$1$': 'r',
            r'$2$': 'g',
            r'$3$': 'c',
            r'$4$': 'y',
        }
    G = nx.Graph( edges )
    pos = nx.spring_layout(G)
    nx.draw_networkx_nodes(G, pos, nodelist=range(nodes), node_color = [colors[label] for _, label in labels.items()])
    nx.draw_networkx_edges(G, pos, width=1.0, alpha=0.5)
    nx.draw_networkx_labels(G,pos,labels,font_size=16)


def negative_example():
    nodes = 7
    edges = [(0,1), (1,2), (2,3), (3,4), (4,5), (5,6)]
    labels = \
        {
            0: r'$1$',
            1: r'$1$',
            2: r'$2$',
            3: r'$2$',
            4: r'$3$',
            5: r'$3$',
            6: r'$4$'
        }
    draw_graph(nodes, edges, labels)


def positive_example():
    nodes = 8
    edges = generateClique([0]) + generateClique([1,2,3]) + generateClique([4,5,6,7]) + [(0, 2), (3, 7)]
    labels = \
        {
            0: r'$1$',
            1: r'$2$',
            2: r'$2$',
            3: r'$2$',
            4: r'$3$',
            5: r'$3$',
            6: r'$3$',
            7: r'$3$'
        }
    draw_graph(nodes, edges, labels)


positive_example()
plt.savefig('pozytywny.png')
# plt.show()
negative_example()
plt.savefig('negatywny.png')
# plt.show()
