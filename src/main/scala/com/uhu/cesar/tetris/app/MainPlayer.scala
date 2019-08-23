package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.QFunction.QFunctionLoader
import com.uhu.cesar.tetris.QPlayer

object MainPlayer extends App with QFunctionLoader {

  val qfunction = loadQFunction(args(0))
  val player = QPlayer(training = false, Some(qfunction))
  player.start()
  player.gameLoop()

}
