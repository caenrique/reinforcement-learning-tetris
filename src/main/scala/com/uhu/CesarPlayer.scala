package com.uhu

import com.uhu.CesarPlayer.{Action, Policy}
import com.uhu.Message.MessageParser
import com.uhu.QFunction.{QFunctionKey, QFunctionLoader, QFunctionSerializer}
import com.uhu.app.auxiliar.Respuesta

import scala.util.Random

class CesarPlayer extends Player with MessageParser with QFunctionSerializer with QFunctionLoader {

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  var qfunction = QFunction(Map.empty)
  var firstMove = true
  var lastMove: QFunctionKey = (ConditionalBoard(Array.fill(Board.WIDTH - 1)(false)), (0, 0))

  override def init(): Unit = {}

  override def restartPolicy(): Boolean = {
    write(qfunction)
    true
  }

  override def think(percepcion: Message): Respuesta = {

    def onPolicy: Policy = actions => {
      val choice = Random.shuffle(actions).maxBy(_._2)._1
      choice
    }

    def randomPolicy: Policy = actions => {
      val choice = Random.shuffle(actions).headOption.map(_._1).getOrElse(0 -> 0)
      choice
    }

    def eGreedy: Policy = if (Random.nextFloat() > 0.2) onPolicy else randomPolicy

    val nextAction = percepcion match {
      case MovMessage(current, _, _, clearedRows, board) =>
        val conditionalBoard = Board.conditionalProjection(board, current)
        if (firstMove) {
          firstMove = false
          val nextAction = play(eGreedy)(qfunction, current, conditionalBoard)
          lastMove = (conditionalBoard, nextAction)
          nextAction
        } else {
          val (newQFunction, nextAction) = train(eGreedy)(qfunction, lastMove, current, clearedRows, conditionalBoard)
          lastMove = (conditionalBoard, nextAction)
          qfunction = newQFunction
          nextAction
        }
      case _ => (0, 0)
    }

    new Respuesta(nextAction._1, nextAction._2)
  }

  def train(policy: Policy)(qFunction: QFunction, lastMove: QFunctionKey, figure: Figure, clearedRows: Int, board: ConditionalBoard): (QFunction, Action) = {

    def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 1 else if (rows == 2) 3 else if (rows == 3) 5 else 8

    val alpha = 0.2f
    val gamma = 0.2f
    val lastMoveValue = qFunction.get(lastMove)

    val difference = alpha * (reward(clearedRows) + gamma * qFunction.bestActionValue(board) - lastMoveValue)

    val nextQFunction = qFunction.update(lastMove, lastMoveValue + difference)

    val nextMove = play(policy)(qFunction, figure, board)
    (nextQFunction, nextMove)
  }

  def play(policy: Policy)(qFunction: QFunction, figure: Figure, board: ConditionalBoard): Action = {
    val keyList = figure.moves.map(_ -> 0)
      .map(action => board -> action)
      .map { case k@(_, a) => a -> qFunction.get(k) }
      .toSeq

    policy(keyList)
  }

}

object CesarPlayer {

  type Action = (Int, Int)
  type Policy = Seq[(Action, Float)] => Action

}
