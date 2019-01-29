package com.uhu

import com.uhu.Player.Action
import com.uhu.QFunction.QFunctionKey

case class Trainer(alpha: Float,
                   gamma: Float,
                   var qf: QFunction = QFunction.empty,
                   var lastMove: Option[QFunctionKey] = None)
                  (play: (QFunction, Figure, ConditionalBoard) => Action) {

  def training(board: ConditionalBoard, figure: Figure, clearedRows: Int): Action = {

    val optionQf = lastMove.map { lm =>
      val lastMoveValue = qf.get(lm)

      // Compute the new value of this state-action pair
      val difference = alpha * (reward(clearedRows) + gamma * qf.bestActionValue(board) - lastMoveValue)

      qf.update(lm, lastMoveValue + difference)
    }

    val nextQFunction = optionQf.getOrElse(qf)
    val nextMove = play(nextQFunction, figure, board)

    updateLastMove((board, nextMove))
    updateQFunction(nextQFunction)

    nextMove
  }

  private def updateLastMove(move: QFunctionKey): Unit = lastMove = Some(move)

  private def updateQFunction(newQf: QFunction): Unit = qf = newQf

  private def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 1 else if (rows == 2) 3 else if (rows == 3) 5 else 8
}

