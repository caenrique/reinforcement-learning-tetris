package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Movement, Rotation}
import com.uhu.cesar.tetris.Board.HeuristicValue
import com.uhu.cesar.tetris.Message.{MessageParser, MovMessage}
import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionSerializer, QFunctionValue}

case class QPlayer(training: Boolean, qf: Option[QFunction]) extends Player
  with MessageParser
  with QFunctionSerializer {

  private val trainer = Trainer(
    0.2f, 0.4f, qf.getOrElse(QFunction.empty)
  )(Policy.heuristicEGreedy(0.1d))

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  override def init(): Unit = {}

  //override def training() = this.training

  override def restart(): Unit = {
    trainer.endOfEpisode()

    println(trainer.statistics)
    start()
  }

  override def think(percepcion: Message): Respuesta = {

    val action = percepcion match {
      case MovMessage(figure, _, _, clearedRows, board) =>
        if (training) trainer.training(board, figure, clearedRows)
        else QPlayer.play(Policy.onPolicy)(qf.getOrElse(QFunction.empty), board, figure)
      case _ => Action(Movement(0), Rotation(0))
    }

    Respuesta(action.movement, action.rotation)
  }

  def writeQFunction: Unit = super.writeQFunction(trainer.qf)

}

object QPlayer {

  def getMoves(board: Board, figure: Figure): List[Action] = {

    (0 to 3).flatMap(r =>
      figure.moves(Rotation(r))
        .map(Action(_, r))
        .filterNot(board.illegalMove(figure, _))
    ).toList
  }

  def play(policy: Policy)(qFunction: QFunction, board: Board, figure: Figure): Action = {

    def qvalue(action: Action): QFunctionValue = action match {
      case Action(movement, rotation) =>
        val key = (board.simpleProjection, figure.symbol, movement, rotation)
        qFunction.get(key)
    }

    def hvalue(action: Action): HeuristicValue = board.computeNextBoard(figure, action).heuristicEval

    policy(getMoves(board, figure).map { a => (a, qvalue(a), hvalue(a)) })
  }

}
