package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.QFunction.QFunctionLoader
import com.uhu.cesar.tetris.{Player, QPlayer}

object MainTraining extends App with QFunctionLoader {

    val qfunctionOption = if (args.nonEmpty) Some(loadQFunction(args(0))) else None
    val jugador: Player = QPlayer(training = true, qfunctionOption)
    //val jugador = new Jug_Teclado();
    jugador.start()
    jugador.gameLoop()

}
