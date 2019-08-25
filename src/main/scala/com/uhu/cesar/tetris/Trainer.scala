package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionKey, QFunctionValue}

case class Trainer(alpha: Float,
                   gamma: Float,
                   var qf: QFunction = QFunction.empty,
                   var lastMove: Option[QFunctionKey] = None)
                  (policy: Int => Policy) {

  def variableAlpha(episode: Int): Float = 1 / (episode + 1)

  def training(episode: Int, board: Board, figure: Figure, clearedRows: Int): Action = {

    val simpleBoard = board.simpleProjection
    val bestValue = qf.bestActionValue(simpleBoard, figure)
    val nextQFunction = mapLastMove(lastMove, computeNewQValue(reward(clearedRows), bestValue, _))
    val nextMove = QPlayer.play(policy(episode))(nextQFunction, board, figure)

    // UPDATING VARIABLES //
    lastMove = Some((simpleBoard, figure.symbol, nextMove.movement, nextMove.rotation))
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
    qf = mapLastMove(lastMove, computeNewQValue(-1000, 0, _))
  }

  private def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 100 else if (rows == 2) 300 else if (rows == 3) 500 else 800
}

