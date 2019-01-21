package com.uhu

import com.uhu.app.auxiliar.{Jugador, Respuesta}

import scala.util.Random

class CesarPlayer extends Jugador("cesarantonio.enrique", "Cesar") {

  override def inicializar(): Unit = {

  }

  override def pensar(percepcion: String): Respuesta = {
    val mov = Random.nextInt(12) - 6
    val rot = Random.nextInt(4)

    println(s"desplazamiento: $mov, rotación: $rot")
    new Respuesta(mov, rot)
  }
}
