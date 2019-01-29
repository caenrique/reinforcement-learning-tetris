package com.uhu

import com.uhu.Message.MessageParser
import com.uhu.Player.Action
import com.uhu.Policy.Policy
import com.uhu.QFunction.{QFunctionLoader, QFunctionSerializer}
import com.uhu.app.auxiliar.Respuesta

class QPlayer extends Player with MessageParser with QFunctionSerializer with QFunctionLoader {

  private val trainer = Trainer(0.2f, 0.2f)(QPlayer.play(Policy.eGreedy))

  override val NAME = "Cesar"
  override val LOGIN = "cesarantonio.enrique"

  override def init(): Unit = {}

  override def restartPolicy(): Boolean = {
    write(trainer.qf)
    true
  }

  override def think(percepcion: Message): Respuesta = {

    val (desp, rot) = percepcion match {
      case MovMessage(figure, _, _, clearedRows, board) =>
        val conditionalBoard = Board.conditionalProjection(board, figure)
        trainer.training(conditionalBoard, figure, clearedRows)
      case _ => (0, 0)
    }

    new Respuesta(desp, rot)
  }

}

object QPlayer {

  def play(policy: Policy)(qFunction: QFunction, figure: Figure, board: ConditionalBoard): Action = {
    val keyList = figure.moves.map(_ -> 0)
      .map(action => board -> action)
      .map { case k@(_, a) => a -> qFunction.get(k) }
      .toSeq

    policy(keyList)
  }

}
