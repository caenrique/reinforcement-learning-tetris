package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Movement, Rotation}
import com.uhu.cesar.tetris.Board.HeuristicValue
import com.uhu.cesar.tetris.Message.{MessageParser, MovMessage}
import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionSerializer, QFunctionValue}

case class QPlayer(training: Boolean, qf: Option[QFunction]) extends Player
  with MessageParser
  with QFunctionSerializer
  with TetrisStats {

  private val trainer = Trainer(
    0.2f, 0.4f, qf.getOrElse(QFunction.empty), episode = currentEpisode
  )(Policy.heuristicEGreedy(0.1d))

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  override def init(): Unit = {}

  override def restart(): Unit = {
    trainer.endOfEpisode()
    recordEndOfGame()
    if(training) println(trainer.statistics)
    start()
  }

  override def think(percepcion: Message): Respuesta = {

    val action = percepcion match {
      case MovMessage(figure, nextFigure, _, clearedRows, board) =>
        recordMove()
        if (clearedRows > 0) recordLine(clearedRows)

        if (training) trainer.training(board, figure, nextFigure, clearedRows)
        else QPlayer.play(Policy.onPolicy)(qf.getOrElse(QFunction.empty), board, figure, nextFigure)
      case _ => Action(Movement(0), Rotation(0))
    }

    Respuesta(action.movement, action.rotation)
  }

  def writeQFunction(): Unit = super.writeQFunction(trainer.qf)

  def printStats(): Unit = {
    println(s"average moves: ${get_averageMovesPerGame()}, average lines: ${get_averageLinesPerGame()}")
  }

}

object QPlayer {

  def play(policy: Policy)(qFunction: QFunction, board: Board, figure: Figure, nextFigure: Figure): Action = {

    def getMoves(board: Board, figure: Figure, nextFigure: Figure): List[(Action, QFunctionValue, HeuristicValue)] = {
      board.computeNextBoardWActions(figure).map { case (action, nextBoard) =>
        val qandh = (0 to 3).flatMap(r => nextFigure.moves(Rotation(r)).map(Action(_, r)))
          .map(a => (qvalue(nextBoard, nextFigure, a), hvalue(nextBoard, nextFigure, a))).toList

        val (qvalues, hvalues) = qandh.unzip

        (action, qvalues.max, hvalues.max)
      }
    }

    def qvalue(b: Board, f: Figure, a: Action): QFunctionValue = a match {
      case Action(movement, rotation) =>
        val key = (b.simpleProjection(f, rotation), movement)
        qFunction.get(key)
    }

    def hvalue(b: Board, f: Figure, a: Action): HeuristicValue = b.computeNextBoard(f, a).heuristicEval

    policy(getMoves(board, figure, nextFigure))
  }

}
