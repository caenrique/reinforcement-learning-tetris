package com.uhu.cesar.tetris.app

object CLIParser {
  val usage =
    """
    Usage: mmlaln [--min-size num] [--max-size num] filename
  """
  type OptionMap = Map[Symbol, Any]

  val QFUNCTION = 'qfunction
  val EPISODES = 'episodes

  def apply(args: Array[String]): OptionMap = {
    if (args.length == 0) println(usage)
    val arglist = args.toList

    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
      def isSwitch(s: String) = s(0) == '-'

      list match {
        case Nil => map
        case "-qfunction" :: value :: tail =>
          nextOption(map ++ Map(QFUNCTION -> value), tail)
        case "-episodes" :: value :: tail =>
          nextOption(map ++ Map(EPISODES -> value.toInt), tail)
        case option :: tail =>
          println("Unknown option " + option)
          println(usage)
          sys.exit(1)
      }
    }

    nextOption(Map(), arglist)
  }
}