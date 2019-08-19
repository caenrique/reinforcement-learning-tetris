package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.{Player, QPlayer}

object Main extends App {

    val jugador: Player = QPlayer(training = true)
    jugador.start()
    jugador.gameLoop()

}
