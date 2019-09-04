package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.QFunction.{QFunctionLoader, QFunctionSerializer}
import com.uhu.cesar.tetris.QPlayer
import com.uhu.cesar.tetris.app.CLIParser.TrainingOptions
import sun.misc.Signal

object Main extends App with QFunctionLoader with QFunctionSerializer {

  val options = CLIParser(args)
  val qfunctionOption = options.qfunction.map(s => loadQFunction(s))
  val trainingOptions = options.trainingOptions.getOrElse(TrainingOptions(alpha = 0.2d, gamma = 0.4d))

  val player: QPlayer = QPlayer(training = options.training, qfunctionOption, trainingOptions)

  // If we stop it while running, it will save the qfunction and print stats
  Signal.handle(new Signal("INT"), _ => {
    player.writeQFunction(options.qfunction)
    if (options.stats) player.printStats()
    System.exit(0)
  })

  player.start()
  player.gameLoop(options.episodes)
  player.printStats()

}
