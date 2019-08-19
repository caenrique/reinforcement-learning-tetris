package com.uhu.cesar.tetris.app

import com.uhu.cesar.tetris.Player.{Displacement, Rotation}

case class Respuesta(desplazar: Displacement, rotar: Rotation) {
  override def toString: String = s"mov;${desplazar.value};${rotar.value};"
}
