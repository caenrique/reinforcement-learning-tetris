package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Movement, Rotation}

case class Action(movement: Movement, rotation: Rotation)

object Action {

  def apply(displacement: Int, rotation: Int): Action = new Action(Movement(displacement), Rotation(rotation))

  case class Rotation(value: Int) {
    assert(value >= 0 && value <= 3)
  }

  case class Movement(value: Int) {
    assert(value >= -6 && value <= 6)
  }

}

