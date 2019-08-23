package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Board.{HeuristicValue, RawBoard}
import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionKey, QFunctionValue}

case class Trainer(alpha: Float,
                   gamma: Float,
                   var qf: QFunction = QFunction.empty,
                   var lastMove: Option[QFunctionKey] = None)
                  (policy: Policy) {

  def training(rawBoard: RawBoard, figure: Figure, clearedRows: Int): Action = {

    val theMoves = QPlayer.getMoves(rawBoard, figure)
    val bestMoveValue = theMoves.map{ case (_, b) => qf.get(Board.simpleProjection(b)) }.max
    val nextQFunction = mapLastMove(lastMove, computeNewQValue(reward(clearedRows), bestMoveValue, _))
    val nextMove = QPlayer.play(policy)(nextQFunction, theMoves)

    // UPDATING VARIABLES //
    lastMove = Some(Board.simpleProjection(theMoves.filter{ case (action, _) => action == nextMove }.head._2))
    qf = nextQFunction
    ////////////////////////

    nextMove
  }

  def computeNewQValue(reward: Int, potential: QFunctionValue, lastMove: QFunctionKey): QFunctionValue = {
    def difference(reward: Int, potential: QFunctionValue, lastValue: QFunctionValue): QFunctionValue = {
      alpha * (reward + (gamma * potential) - lastValue)
    }

    val lastMoveValue = qf.get(lastMove)
    lastMoveValue + difference(reward, potential, lastMoveValue)
  }

  def mapLastMove(lmo: Option[QFunctionKey], newValueF: QFunctionKey => QFunctionValue): QFunction = {
    lmo.map(lm => qf.update(lm, newValueF(lm))).getOrElse(qf)
  }

  def endOfEpisode(): Unit = {
    qf = mapLastMove(lastMove, computeNewQValue(-100, 0, _))
  }

  private def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 100 else if (rows == 2) 300 else if (rows == 3) 500 else 800
}

