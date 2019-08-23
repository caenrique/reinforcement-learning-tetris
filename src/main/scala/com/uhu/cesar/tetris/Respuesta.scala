package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Displacement, Rotation}

case class Respuesta(desplazar: Displacement, rotar: Rotation) {
  override def toString: String = s"mov;${desplazar.value};${rotar.value};"
}
