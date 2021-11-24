import Graph.PersonCollisions

import scala.collection.mutable.ListBuffer

package object Graph {
    type Indexed = Map[String, ListBuffer[Int]]

    class PersonCollisions {

        def loadCsvFile(path : String) : List[(Int, Int, Int)] =
        {
            val fileHandler = scala.io.Source.fromFile(path)
            val rawFile = try fileHandler.mkString finally fileHandler.close()
            return rawFile.split("\n").map(x => x.split(",").map(y => y.toInt)).map(x => (x(0), x(1), x(2))).toList
        }

        def findPeopleWithDaysAndHotelsWithoutCollision(data : List[(Int, Int, Int)]) : List[(Int, Int)] =
        {
            return data.groupBy(x => (x._1, x._2)).mapValues(x => x.map(y => y._3).
                toSet.subsets(2).map(x => (x, 1)).toList).map(x=>x._2).reduce((x,y)=>x++y).
                    groupBy(x=>x._1).mapValues(x=>x.length).filter(x=>x._2==2).toList.map(x=>x._1.toList).map(x=>(x(0), x(1)))
        }
    }
}

object Main {
    def main(args: Array[String]) {
        val s = new PersonCollisions
        val graph = s.loadCsvFile("TwoCollisions.csv")
        val r = s.findPeopleWithDaysAndHotelsWithoutCollision(graph)
        for (x <- r) printf("(%s, %s)\n", x._1, x._2)

    }
}