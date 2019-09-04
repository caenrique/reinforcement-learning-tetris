package com.uhu.cesar.tetris.app

object CLIParser {

  case class TetrisOptions(
                          training: Boolean,
                          stats: Boolean,
                          trainingOptions: Option[TrainingOptions],
                          qfunction: Option[String],
                          episodes: Option[Int]
                          )

  case class TrainingOptions(alpha: Double, gamma: Double)

  val usage =
    """
    Usage: program-name [--training [alpha gamma]] [--stats] [--qfunction filename] [--episodes integer]
  """

  def apply(args: Array[String]): TetrisOptions = {

    val arglist = args.toList

    @scala.annotation.tailrec
    def nextOption(options: TetrisOptions, list: List[String]): TetrisOptions = {
      list match {
        case Nil => options
        case "--training" :: alpha :: gamma :: tail =>
          if (alpha(0) != '-' && gamma(0) != '-') {
            nextOption(options.copy(
              training = true,
              trainingOptions = Some(TrainingOptions(alpha.toDouble, gamma.toDouble))
            ), tail)
          } else {
            nextOption(options.copy(training = true), tail)
          }
        case "--stats" :: tail =>
          nextOption(options.copy(stats = true), tail)
        case "--qfunction" :: value :: tail =>
          nextOption(options.copy(qfunction = Some(value)), tail)
        case "--episodes" :: value :: tail =>
          nextOption(options.copy(episodes = Some(value.toInt)), tail)
        case option :: _ =>
          println("Unknown option " + option)
          println(usage)
          sys.exit(1)
      }
    }

    nextOption(TetrisOptions(training = false, stats = false, None, None, None), arglist)
  }
}