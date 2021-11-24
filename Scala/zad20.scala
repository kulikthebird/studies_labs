import Graph.InvertedIndex

package object Graph {
    type Indexed = Map[String, List[Int]]

    class InvertedIndex {

        def splitFileToWords(path : String) : Array[String] =
        {
            val fileHandler = scala.io.Source.fromFile(path)
            val rawFile = try fileHandler.mkString finally fileHandler.close()
            return rawFile.split("""( |\n|\.|[0-9]|-|,|:|\(|\)|\?|!|;|\'|‘)+""")
        }

        def createInvertIndexDocuments(docs : Array[String]) : Indexed =
        {
            return docs.zipWithIndex.map(x => splitFileToWords(x._1).toSet.map((y:String) => (y, x._2))).
                reduce((x,y)=>x++y).toList.groupBy(x => x._1).mapValues(x => x.map(y => y._2))
        }

        def printGraph(g: Indexed) = {
            for ((i, rest) <- g) {
                printf("%s\t[", i)
                for (x <- rest) printf(" %s", x)
                printf(" ]\n")
            }
        }
    }
}

object Main {
    def main(args: Array[String]) {
        val s = new InvertedIndex
        val graph = s.createInvertIndexDocuments(Array("doc1.txt", "doc2.txt", "doc3.txt"))
        s.printGraph(graph)
    }
}