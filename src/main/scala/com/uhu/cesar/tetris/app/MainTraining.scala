package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.QFunction.{QFunctionLoader, QFunctionSerializer}
import com.uhu.cesar.tetris.{QFunction, QPlayer}
import sun.misc.Signal

object MainTraining extends App with QFunctionLoader with QFunctionSerializer {

  val qfunctionOption = if (args.nonEmpty) Some(loadQFunction(args(0))) else None
  val player: QPlayer = QPlayer(training = true, qfunctionOption)

  Signal.handle(new Signal("INT"), _ => {
    player.writeQFunction
    System.exit(0)
  })

  player.start()
  player.gameLoop()

}
