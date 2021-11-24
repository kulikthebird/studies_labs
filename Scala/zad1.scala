
class Cloud
{
    def splitFileToWords(path : String) : Array[String] =
    {
        val fileHandler = scala.io.Source.fromFile(path)
        val rawFile = try fileHandler.mkString finally fileHandler.close()
        rawFile.split("""( |\n|\.|[0-9]|-|,|:|\(|\)|\?|!|;|\'|‘)+""")
    }

    def chooseCommonWords(text : Array[String], stopWords : Array[String]) : Seq[(String, Int)] =
    {
        val bookFiltered = text.filterNot((x : String) => stopWords.contains(x.toLowerCase))
        bookFiltered.groupBy(x=>x).mapValues(x => x.length).toSeq.sortWith((x,y)=>x._2>y._2)
    }

    def chooseWordsFromRange(sortedWords : Seq[(String, Int)]) =
        sortedWords.filter( {case (x : String, y : Int) => y >= 35 && y <=65} )

    def countTf(i : Int, words : Seq[(String, Int)], chapter : Int) =
        for(p <- words) words.reduce((x, y) => ("żopa", x._2 + y._2))
}


object Main
{
    def main(args: Array[String])
    {
        var c = new Cloud()
        val sortedWords = c.chooseCommonWords(c.splitFileToWords("silmarillion.txt"), c.splitFileToWords("stopwords.txt"))
        println("Wydruk do zadania pierwszego:")
        for ((k,v) <- c.chooseWordsFromRange(sortedWords)) printf("%s\t%s\n", v, k)
    }
}
