package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.Rotation
import org.scalatest.{FlatSpec, Matchers}

class FigureSpec extends FlatSpec with Matchers {

  "A Figure" should "compute the correct moves given a rotation" in {
    Figure.SQUARE.moves(Rotation(0)) shouldEqual (-4 to 4).toList
  }

}
