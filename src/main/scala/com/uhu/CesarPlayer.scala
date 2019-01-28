package com.uhu

import com.uhu.Message.MessageParser
import com.uhu.app.auxiliar.Respuesta

import scala.util.Random

class CesarPlayer extends Player with MessageParser {

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  type Action = (Int, Int)
  type Policy = Seq[(Action, Float)] => Action

  type QFunctionKey = (ConditionalBoard, Action)
  type QFunctionValue = Float
  type QFunction = Map[QFunctionKey, QFunctionValue]

  var qfunction: QFunction = Map.empty
  var firstMove = true
  var lastMove: QFunctionKey = (ConditionalBoard(Array.fill(Board.WIDTH - 1)(false)), (0, 0))

  override def init(): Unit = {}

  override def think(percepcion: Message): Respuesta = {

    def onPolicy: Policy = actions => {
      val choice = Random.shuffle(actions).maxBy(_._2)._1
      println(choice)
      choice
    }

    def randomPolicy: Policy = actions => {
      val choice = Random.shuffle(actions).headOption.map(_._1).getOrElse(0 -> 0)
      println(choice)
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

    println(s"desplazamiento: ${nextAction._1}, rotación: ${nextAction._2}")
    new Respuesta(nextAction._1, nextAction._2)
  }

  def train(policy: Policy)(qFunction: QFunction, lastMove: QFunctionKey, figure: Figure, clearedRows: Int, board: ConditionalBoard): (QFunction, Action) = {

    def reward(rows: Int): Int = if (rows == 0) 0 else if (rows == 1) 1 else if (rows == 2) 3 else if (rows == 3) 5 else 8

    def bestActionValue(qf: QFunction, b: ConditionalBoard): QFunctionValue = {
      val maybeEmpty = qFunction.filter { case ((b, _), _) => b == board }
      if (maybeEmpty.isEmpty) 0f else maybeEmpty.maxBy(_._2)._2
    }

    val alpha = 0.2f
    val gamma = 0.2f
    val lastMoveValue = qFunction.getOrElse(lastMove, 0f)

    val difference = alpha * (reward(clearedRows) + gamma * bestActionValue(qFunction, board) - lastMoveValue)

    val nextQFunction = qFunction.updated(lastMove, lastMoveValue + difference)

    val nextMove = play(policy)(qFunction, figure, board)
    (nextQFunction, nextMove)
  }

  def play(policy: Policy)(qFunction: QFunction, figure: Figure, board: ConditionalBoard): Action = {
    val keyList = figure.moves.map(_ -> 0)
      .map(action => board -> action)
      .map { case k@(_, a) => a -> qFunction.getOrElse(k, 0f) }
      .toSeq

    policy(keyList)
  }

}
