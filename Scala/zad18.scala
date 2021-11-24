import Graph.GraphInverse

package object Graph {
    type Graph = List[(Int, List[Int])]

    class GraphInverse {
        def loadGraphFile(path: String): Graph = {
            val fileHandler = scala.io.Source.fromFile(path)
            val rawFile = try fileHandler.mkString finally fileHandler.close()
            return rawFile.split("\n").toList.map(x => x.split(" ")).
                map(x => (x(0).toInt, x.drop(1).toList.map(x => x.toInt)))
        }

        def printGraph(g: Graph) = {
            for ((i, rest) <- g) {
                printf("%s [", i)
                for (x <- rest) printf(" %s", x)
                printf(" ]\n")
            }
        }

        def inverseGraph(g: Graph): Graph =
            g.map(x => x._2.map(k => (x._1, k))).reduce((x, y) => x ++ y).
                map(x => (x._2, x._1)).groupBy(x => x._1).toList.map(x => (x._1, x._2.map(y => y._2)))
    }
}

object Main {
    def main(args: Array[String]) {
        val s = new GraphInverse
        val graph = s.loadGraphFile("graph.txt")
        s.printGraph(graph)
        printf("\n")
        s.printGraph(s.inverseGraph(graph))
    }
}