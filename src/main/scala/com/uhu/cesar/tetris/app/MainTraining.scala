package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.QFunction.{QFunctionLoader, QFunctionSerializer}
import com.uhu.cesar.tetris.QPlayer
import sun.misc.Signal

object MainTraining extends App with QFunctionLoader with QFunctionSerializer {

  val options = CLIParser(args)
  val qfunctionOption = options.get(CLIParser.QFUNCTION).asInstanceOf[Option[String]].map(s => loadQFunction(s))
  val episodes = options.get(CLIParser.EPISODES).asInstanceOf[Option[Int]]

  val player: QPlayer = QPlayer(training = true, qfunctionOption)

  Signal.handle(new Signal("INT"), _ => {
    player.writeQFunction()
    System.exit(0)
  })

  player.start()
  player.gameLoop()

}
