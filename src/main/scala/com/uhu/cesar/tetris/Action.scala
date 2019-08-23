package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Displacement, Rotation}

case class Action(movement: Displacement, rotation: Rotation)

object Action {

  def apply(displacement: Int, rotation: Int): Action = new Action(Displacement(displacement), Rotation(rotation))

  case class Rotation(value: Int) {
    assert(value >= 0 && value <= 3)
  }

  case class Displacement(value: Int) {
    assert(value >= -6 && value <= 6)
  }

}

