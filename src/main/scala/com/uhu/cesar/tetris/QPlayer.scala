package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.{Displacement, Rotation}
import com.uhu.cesar.tetris.Board.{HeuristicValue, RawBoard}
import com.uhu.cesar.tetris.Message.{MessageParser, MovMessage}
import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionLoader, QFunctionSerializer, QFunctionValue}

case class QPlayer(training: Boolean, qf: Option[QFunction] = None) extends Player
  with MessageParser
  with QFunctionSerializer
  with QFunctionLoader {

  private val trainer = Trainer(
    0.2f, 0.2f, qf.getOrElse(QFunction.empty)
  )(Policy.heuristicEGreedy)

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  override def init(): Unit = {}

  override def restart(): Boolean = {
    trainer.endOfEpisode()
    writeQFunction(trainer.qf)
    true
  }

  override def think(percepcion: Message): Respuesta = {

    val action = percepcion match {
      case MovMessage(figure, _, _, clearedRows, board) =>
        if (training) trainer.training(board, figure, clearedRows)
        else QPlayer.play(Policy.eGreedy)(qf.getOrElse(QFunction.empty), QPlayer.getMoves(board, figure))
      case _ => Action(Displacement(0), Rotation(0))
    }

    Respuesta(action.movement, action.rotation)
  }

}

object QPlayer {

  def getMoves(board: RawBoard, figure: Figure): List[(Action, RawBoard)] = {

    (0 to 3).flatMap(r =>
      Figure.moves(figure, Rotation(r))
        .map(Action(_, r))
        .filterNot(Board.illegalMove(board, figure, _))
        .map { a => (a, Board.computeNextBoard(board, figure, a)) }
    ).toList
  }

  def play(policy: Policy)(qFunction: QFunction, moves: List[(Action, RawBoard)]): Action = {

    def qvalue(b: RawBoard): QFunctionValue = qFunction.get(Board.simpleProjection(b))
    def hvalue(b: RawBoard): HeuristicValue = Board.heuristicEval(b)

    policy(moves.map { case (a, b) => (a, qvalue(b), hvalue(b)) })
  }

}
