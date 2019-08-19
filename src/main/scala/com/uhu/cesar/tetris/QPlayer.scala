package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Board.HeuristicValue
import com.uhu.cesar.tetris.Message.MessageParser
import com.uhu.cesar.tetris.Player.{Action, Displacement, Rotation}
import com.uhu.cesar.tetris.Policy.Policy
import com.uhu.cesar.tetris.QFunction.{QFunctionKey, QFunctionLoader, QFunctionSerializer, QFunctionValue}
import com.uhu.cesar.tetris.app.Respuesta

case class QPlayer(training: Boolean, qf: Option[QFunction] = None) extends Player
  with MessageParser
  with QFunctionSerializer
  with QFunctionLoader {

  private val trainer = Trainer(
    0.2f, 0.2f, qf.getOrElse(QFunction.empty)
  )( QPlayer.play(Policy.heuristicEGreedy) )

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  override def init(): Unit = {}

  override def restart(): Boolean = {
    trainer.endOfEpisode()
    //write(trainer.qf)
    true
  }

  override def think(percepcion: Message): Respuesta = {

    val action = percepcion match {
      case MovMessage(figure, _, _, clearedRows, board) =>
        if (training) trainer.training(board, figure, clearedRows)
        else QPlayer.play(Policy.eGreedy)(qf.getOrElse(QFunction.empty), figure, board)
      case _ => Action(Displacement(0), Rotation(0))
    }

    Respuesta(action.movement, action.rotation)
  }

}

object QPlayer {

  def play(policy: Policy)(qFunction: QFunction, figure: Figure, board: RawBoard): Action = {

    def qvalue(a: Action): QFunctionValue = qFunction.get(QFunctionKey(Board.simpleProjection(board), figure, a))
    def hvalue(a: Action): HeuristicValue = Board.heuristicEval(Board.computeNextBoard(board, figure, a))

    val keyList = figure.moves.map(Action(_, 0)).map { a => ( a, qvalue(a), hvalue(a) ) }.toSeq

    policy(keyList)
  }

}
