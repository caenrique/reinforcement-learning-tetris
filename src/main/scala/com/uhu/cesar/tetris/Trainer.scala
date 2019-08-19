package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Player.Action
import com.uhu.cesar.tetris.QFunction.QFunctionKey

case class Trainer(alpha: Float,
                   gamma: Float,
                   var qf: QFunction = QFunction.empty,
                   var lastMove: Option[QFunctionKey] = None)
                  (play: (QFunction, Figure, RawBoard) => Action) {

  def training(rawBoard: RawBoard, figure: Figure, clearedRows: Int): Action = {

    val board = Board.simpleProjection(rawBoard)

    val nextQFunction = mapLastMove(lastMove, computeNewQValue(reward(clearedRows), qf.bestActionValue(board), _))
    val nextMove = play(nextQFunction, figure, rawBoard)

    // UPDATING VARIABLES //
    lastMove = Some(QFunctionKey(board, figure, nextMove))
    qf = nextQFunction
    ////////////////////////

    nextMove
  }

  def computeNewQValue(reward: Int, potential: Float, lastMove: QFunctionKey): Float = {
    def difference(reward: Int, potential: Float, lastValue: Float): Float = {
      alpha * (reward + (gamma * potential) - lastValue)
    }
    val lastMoveValue = qf.get(lastMove)
    lastMoveValue + difference(reward, potential, lastMoveValue)
  }

  def mapLastMove(lmo: Option[QFunctionKey], newValueF: QFunctionKey => Float): QFunction = {
    lmo.map(lm => qf.update(lm, newValueF(lm))).getOrElse(qf)
  }

  def endOfEpisode(): Unit = {
    qf = mapLastMove(lastMove, computeNewQValue(-100, 0, _))
  }

  private def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 100 else if (rows == 2) 300 else if (rows == 3) 500 else 800
}

