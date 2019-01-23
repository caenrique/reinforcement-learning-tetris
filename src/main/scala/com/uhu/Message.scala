package com.uhu

import com.uhu.Board.BoardParser

sealed trait Message
case class MovMessage(current: Figure, next: Figure, score: Int, clearedRows: Int, board: Board) extends Message
case class EndMessage(finalScore: Int) extends Message
case object BadMessage extends Message

object Message {

  trait MessageParser extends BoardParser {

    def parseMessage(message: String): Message = {
      val movPattern = raw"MOV;(\d);(\d);(\d+);(\d+);(\d+)".r
      val endPattern = raw"FIN;[\.\w]+;\w+;(\d+);".r

      println(message)

      message match {
        case movPattern(current, next, board, score, clearedRows) =>
          MovMessage(Figure.SQUARE, Figure.SQUARE, score.toInt, clearedRows.toInt, parseBoard(board))
        case endPattern(score) => EndMessage(score.toInt)
        case _ => BadMessage
      }
    }

  }

}
