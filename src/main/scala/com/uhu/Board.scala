package com.uhu

case class Board(values: Array[Array[Int]])
case class FeaturesBoard(avgHeight: Float, numOfHoles: Int, agrHeightDiff: Int)
case class SimpleBoard(values: Array[Int])
case class ConditionalBoard(fitArray: Array[Boolean])

object Board {

  val HEIGHT = 22
  val WIDTH = 10
  val WALL = 9
  val EMPTY = 0

  val DEFAULT_COLUMN = 4

  trait BoardParser {
    def parseBoard(serializedBoard: String): Board = {
      val boardData = serializedBoard.map(_.toInt).filterNot(_ == WALL).toArray.grouped(HEIGHT).toArray
      println(boardData.map(_.length))
      Board(boardData)
    }
  }

  def featuresProjection(b: Board): FeaturesBoard = {
    def avgHeight: Board => Float = ???
    def numOfHoles: Board => Int = ???
    def agrHeightDiff: Board => Int = ???

    FeaturesBoard(avgHeight(b), numOfHoles(b), agrHeightDiff(b))
  }

  def simpleProjection(b: Board): SimpleBoard = {
    val values = b.values.map(columnHeight)
    SimpleBoard(values)
  }

  def conditionalProjection(board: Board, figure: Figure): ConditionalBoard = {
      def filterBelowColumns(board: Board, figure: Figure, move: Int): Array[Array[Int]] = {
        val start = DEFAULT_COLUMN - move
        val end = start + figure.width
        board.values.slice(start, end)
      }

      def executeMove: Board => Figure => Int => Boolean = b => f => m => {
        val belowColumns = filterBelowColumns(b, f, m).map(columnHeight)
        val newHeights = Figure.computeNewHeights(belowColumns, f)
        newHeights.exists(_ > b.values.map(columnHeight).max)
      }

    ConditionalBoard(figure.moves.map(executeMove(board)(figure)))
  }

  def columnHeight: Array[Int] => Int = _.dropWhile(_ == EMPTY).length
}
