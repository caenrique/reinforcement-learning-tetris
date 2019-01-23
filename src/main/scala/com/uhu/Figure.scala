package com.uhu

case class Figure(symbol: Int, width: Int, moves: Array[Int], grid: Array[Array[Int]])

object Figure {
  val SQUARE = Figure(
    symbol = 3,
    width = 2,
    moves = (-4 to 4).filter(_ % 2 == 0).toArray,
    grid = Array(
      Array(0, 0, 0, 0),
      Array(0, 3, 3, 0),
      Array(0, 3, 3, 0),
      Array(0, 0, 0, 0)
    )
  )

  val EMPTY = Figure(
    symbol = 0,
    width = 0,
    moves = Array.empty,
    grid = Array.ofDim(2)
  )

  def isFigure(symbol: Int): Boolean = symbol == 3

  def apply(symbol: Int): Figure = symbol match {
    case SQUARE.symbol => SQUARE
  }

  def computeNewHeights(partialBoardHeights: Array[Int], figure: Figure): Array[Int] = {
    val newMax = partialBoardHeights.max + 2
    partialBoardHeights.map(_ => newMax)
  }
}
