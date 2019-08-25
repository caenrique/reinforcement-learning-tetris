package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Movement, Rotation}

case class Respuesta(desplazar: Movement, rotar: Rotation) {
  override def toString: String = s"mov;${desplazar.value};${rotar.value};"
}
