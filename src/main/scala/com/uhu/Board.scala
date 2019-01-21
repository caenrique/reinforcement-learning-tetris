package com.uhu

case class Board(values: Array[Array[Int]])
case class FeaturesBoard(avgHeight: Float, numOfHoles: Int, agrHeightDiff: Int)
case class SimpleBoard(values: Array[Int])
case class ConditionalBoard(fitArray: Array[Boolean])

object Board {

  val HEIGHT = 24
  val WIDTH = 12

  trait BoardParser {
    def parseBoard(serializedBoard: String): Board = {
      Board(serializedBoard.map(_.toInt).toArray.grouped(HEIGHT).toArray)
    }
  }

  def featuresProjection(b: Board): FeaturesBoard = {
    def avgHeight: Board => Float = ???
    def numOfHoles: Board => Int = ???
    def agrHeightDiff: Board => Int = ???

    FeaturesBoard(avgHeight(b), numOfHoles(b), agrHeightDiff(b))
  }

  def simpleProjection(b: Board): SimpleBoard = {
    def computeHeight: Seq[Int] => Int = ???

    val values = b.values.map(computeHeight.compose(_.toSeq))
    SimpleBoard(values)
  }

  def conditionalProjection(b: Board, figure: Int): ConditionalBoard = {
    def computeFit: Board => Int => Boolean = ???

    ConditionalBoard((0 to Board.WIDTH).map(computeFit(b)).toArray)
  }
}
