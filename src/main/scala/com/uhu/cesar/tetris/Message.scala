package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Board.{BoardParser, RawBoard}

import scala.util.matching.Regex

sealed trait Message

object Message {

  case class MovMessage(current: Figure, next: Figure, score: Int, clearedRows: Int, board: RawBoard) extends Message
  case class EndMessage(finalScore: Int) extends Message
  case class BadMessage(msg: String) extends Message

  trait MessageParser extends BoardParser {

    def parseMessage(message: String): Message = {
      val movPattern: Regex = raw"MOV;(\d);(\d);(\d+);(\d+);(\d+)".r
      val endPattern = raw"FIN;[\.\w]+;\w+;(\d+);".r

      message match {
        case movPattern(current, next, board, score, clearedRows) =>
          MovMessage(Figure(current.toInt), Figure(next.toInt), score.toInt, clearedRows.toInt, parseBoard(board))
        case endPattern(score) => EndMessage(score.toInt)
        case _ => BadMessage(message)
      }
    }

  }

}
