import java.io.{File, FileNotFoundException, IOException}
import scala.io.{BufferedSource, Source}
import scala.io.Source
object Main {
  val usage =
    """
    Usage: Configs [--W-w] [Window] [--R-r] [--b] filename
  """

  class FileWithFlags {

    var source: List[String] = _
    var stateRow: Boolean = true
    var stateWindow: String = _
    var stateTotalWindow: Boolean = false
    var stateHelp: Boolean = false
    var stateDenyList: Boolean = false
    var denyList: Array[String] = Array("WHAT", "WHOM") // Default blackList

    def createFileNew (filename: String): Unit = {
      val file: BufferedSource = Source.fromFile(filename)
      this.source = file.getLines().toList
    }

    def setWindow ( window: String): Unit = {
      println(window)
      this.stateWindow = window
    }
    def setW ( state: Boolean): Unit = {
      this.stateTotalWindow = state
    }
    def setB ( state: Boolean): Unit = {
      this.stateDenyList = state
    }
    def setBlackList (prohibitWords: Array[String]): Unit = {
      if (prohibitWords != null) {
        this.denyList = prohibitWords;
      }
    }
    def setHelp (state: Boolean): Unit = {
      this.stateHelp = state
    }

    def windowCounter(window: String): Unit = {
      val diapazone:Array[String] = window.split(',')
      var count = 0
      for (row <- diapazone) {
        if (row.contains('-')) {
          var begin = row.split('-')(0).toInt
          var end = row.split('-')(1).toInt
          if (begin > this.source.length || end > this.source.length || begin*end == 0) {
            println("Illegal source data!")
            sys.exit(1)
          }
          if (begin > end ) {
            val c = end
            end = begin
            begin = end
          }
          else if (begin == end) {
            end = begin + 1
          }
          do {
            count = count + wordCount(this.source(begin-1))
            begin = begin + 1
          } while ((begin-1) != end)
        }
        else {
          if (row.toInt > this.source.length || row.toInt == 0 || row.toInt < 0) {
            println("Illegal source data!")
            sys.exit(1)
          }
          count = count + wordCount(this.source(row.toInt-1))
        }
      }
      print("Count total words: ")
      println(count)
    };

    /**
     *
     * @param file
     */
    def totalWindowCounter(): Unit = {
      var count = 0: Int;
      for (row <- this.source) {
        count = count + this.wordCount(row)
      }
      print("Count total words: ")
      println(count)
    };

    /**
     *
     * @param file
     */
    def rowCounter(): Unit = {
      val size = this.source.length
      print("Count total rows: ")
      println(size)
    };
    /**
     *
     * @param line
     * @return
     */

    def wordCount (line:String): Int = {
      val list = line.split("\\s+")
      var count = 0;
      if (!this.stateDenyList) {
        return list.length
      }
      else {
        for (word <- list) {
          if(!this.denyList.contains(word)) {
            count = count + 1
          }
        }
      }
      count
    }
    def help: Unit = {
      println("*--w -- cont words in row: -w7;6;8 :: by default -> all words in file\n" +
        "*--W -- count all words in file without window*\n" +
        "*--R -- count all rows in file without window*\n" +
        "*--w and --W --> will works together\n" +
        "*--r and --R --> will works together\n" +
        "* --b -- use a blacklist :: without --b flag all configs works without blacklist\n" +
        "* Default -- count rows in file (--R only) -- without -b\n" +
        "--help - All information about utilite configuration ")
    }

  }

  def main(args: Array[String]): Unit = {

    val file = new FileWithFlags()

    /** Read flags in command line
     * */
    if (args.length == 0) {
      println(usage)
      sys.exit(1)
    }
    else {
      for (w <- args.indices) {
        args(w) match {
          case ("-w") => file.setWindow(args(w + 1))
          case ("--word") => file.setWindow(args(w + 1))
          case ("--w") => file.setWindow(args(w + 1))
          case ("-W") => file.setW(true)
          case ("--W") => file.setW(true)
          case ("--WORD") => file.setW(true)
          case ("-b") => file.setB(true)
          case ("--blacklist") => file.setB(true)
          case ("--b") => file.setB(true)
          case ("--h") => file.setHelp(true)
          case ("-h") => file.setHelp(true)
          case ("--help") => file.setHelp(true)
          case _ => None
        }
      }
    }

    /** Create a source for file */
    try {
      file.createFileNew(args(0))
    }
    catch {
      case e: FileNotFoundException => println("Couldn't find that file.")
      case e: IOException => println("Had an IOException trying to read that file")
    }
    val envParameters = sys.env.toList
    val Path = envParameters.toMap.get("PATH").toString
    file.setBlackList(loadXML(Path))
    processingFile(file)
    println("See you later!")

  }

  def processingFile(file: FileWithFlags): Unit = {

    if (file.stateWindow != null && file.stateTotalWindow) {
      file.windowCounter(file.stateWindow)
    }
    else if ((!file.stateTotalWindow) && file.stateWindow != null) {
      file.windowCounter(file.stateWindow)
    }
    else if (file.stateTotalWindow) {
      file.totalWindowCounter()
    }
    if (file.stateRow) {
      file.rowCounter()
    }
    if (file.stateHelp) {
      file.help
    }
  }


  def loadXML(Path: String): Array[String] ={
    var path: String = Path
    if (Path == null) {
      val path = "./config.xml"
    }
    if (!Path.matches(".xml$")) {
      println("Had an IOException trying to read that file")
      return null
    }
    try {
      val file: BufferedSource = Source.fromFile(path)
    }
    catch {
      case e: FileNotFoundException => println("Couldn't find that file.")
      case e: IOException => println("Had an IOException trying to read that file")
        return null
    }
    val file: BufferedSource = Source.fromFile(path)
    val source = file.getLines().toList
    var blacklist:Array[String] = new Array[String](120)
    var i = 0
    for( obj <-source) {
      val word = {
        obj.split("<(?:\"[^\"]*\"['\"]*|'[^']*'['\"]*|[^'\">])+>").toList
      }
      if (word.length >1) {
        blacklist(i) = word(1)
        i = i+1
      }
    }
    blacklist
  }

}
