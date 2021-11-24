import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import java.math._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashSet


class Students
{
    val m = Array(8, 5, 3)
    var res = new ListBuffer[(Int,Int)]()
    var h = new HashSet[String]

    def trans(x : Int, y : Int, d : Array[Int]) : Array[Int] =
    {
        var result = d.clone()
        val delta = math.min(m(x), d(x) + d(y))
        result(y) -= delta - d(x)
        result(x) = delta
        result
    }

    def studenty(state : Array[Int]) : Boolean =
    {
        if(state.deep == Array(4, 4, 0).deep)
            return true
        var currentState = state.clone()
        for(x <- 0 to 2 ; y <- 0 to 2)
        {
            if (x != y)
            {
                currentState = trans(x, y, state)
                if(!h.contains(currentState.deep.mkString(" ")))
                {
                    println(currentState.deep)
                    h.add(currentState.deep.mkString(" "))
                    if(studenty(currentState))
                    {
                        res += ((x, y))
                        return true
                    }
                }
            }
        }
        return false
    }
}


object Main
{
    def main(args: Array[String])
    {
        val s = new Students
        s.studenty(Array(8, 0, 0))
        for ((k,v) <- s.res) printf("%s, %s\n", k, v)
    }
}
