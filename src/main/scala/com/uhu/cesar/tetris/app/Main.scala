package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.QFunction.{QFunctionLoader, QFunctionSerializer}
import com.uhu.cesar.tetris.QPlayer
import sun.misc.Signal

object Main extends App with QFunctionLoader with QFunctionSerializer {


  val options = CLIParser(args)
  val qfunctionOption = options.qfunction.map(s => loadQFunction(s))

  val player: QPlayer = QPlayer(training = options.training, qfunctionOption)

  Signal.handle(new Signal("INT"), _ => {
    player.writeQFunction()
    System.exit(0)
  })

  player.start()
  player.gameLoop(options.episodes)
}
