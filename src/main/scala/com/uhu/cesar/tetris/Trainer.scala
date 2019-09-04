package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionKey, QFunctionValue}

case class Trainer(alpha2: Double,
                   gamma: Double,
                   var qf: QFunction,
                   var lastMove: Option[QFunctionKey] = None,
                   var episode: Int = 1)
                  (policy: Int => Policy) {

  var clearedRows = 0

  def training(board: Board, figure: Figure, nextFigure: Figure, rows: Int): Action = {

    val bestValue = qf.bestActionValue(board, figure)
    val nextQFunction = mapLastMove(lastMove, computeNewQValue(reward(clearedRows), bestValue, _))
    val nextMove = QPlayer.play(policy(episode))(nextQFunction, board, figure, nextFigure)

    val simpleBoard = board.simpleProjection(figure, nextMove.rotation)

    // UPDATING VARIABLES //
    lastMove = Some((simpleBoard, nextMove.movement))
    qf = nextQFunction
    clearedRows = clearedRows + rows
    ////////////////////////

    nextMove
  }

  def statistics: String = {
    val statString = s"episode: $episode, states: ${qf.data.size}, alpha: $alpha, cleared rows: $clearedRows"
    clearedRows = 0
    statString
  }

  def alpha = 1 / (2 + Math.sqrt(episode % 100))

  def computeNewQValue(reward: Int, potential: QFunctionValue, lastMove: QFunctionKey): QFunctionValue = {
    val lastMoveValue = qf.get(lastMove)
    (1 - alpha) * lastMoveValue + alpha * (reward + gamma * potential)
  }

  def mapLastMove(lmo: Option[QFunctionKey], newValueF: QFunctionKey => QFunctionValue): QFunction = {
    lmo.map(lm => qf.update(lm, newValueF(lm))).getOrElse(qf)
  }

  def endOfEpisode(): Unit = {
    qf = mapLastMove(lastMove, computeNewQValue(-100, 0, _))
    episode = episode + 1
  }

  private def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 100 else if (rows == 2) 300 else if (rows == 3) 500 else 800
}

