package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.QFunction.QFunctionKey
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable.HashMap

class TrainerSpec extends FlatSpec with Matchers {

  "A Trainer" should "compute new qvalue given a move, a reward and a potential" in {
    val nextBoard = Board.emptyBoard.computeNextBoard(Figure(3), Action(-4, 0))
    val simpleBoard = Board.emptyBoard.simpleProjection
    val nextSimpleBoard = nextBoard.simpleProjection
    val key: QFunctionKey = simpleBoard
    val nextKey: QFunctionKey = nextSimpleBoard
    val qfunction = QFunction(HashMap(key -> 1.0, nextKey -> 2))
    val trainer = Trainer(0.2f, 0.8f, qfunction, None)(Policy.heuristicEGreedy(0.1d))

    val qvalue0 = trainer.qf.get(key)
    println(s"qValue: $qvalue0")

    val qvalue1 = trainer.computeNewQValue(1, qfunction.bestActionValue(nextBoard, Figure.SQUARE), key)
    println(s"qValue: $qvalue1")

    val qvalue2 = trainer.computeNewQValue(2, qfunction.bestActionValue(Board.emptyBoard, Figure.SQUARE), key)
    println(s"qValue: $qvalue2")
  }
}
