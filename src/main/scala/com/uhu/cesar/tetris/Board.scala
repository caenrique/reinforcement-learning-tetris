package com.uhu.cesar.tetris

import Player.{Action, Rotation}

sealed trait Board extends Product with Serializable
case class RawBoard(values: Vector[Vector[Int]]) extends Board {
  override def toString: String = {
    values.transpose
      .map(_.map(n => if (n == 0) " " else n.toString).mkString(" ", " ", " "))
      .mkString("#", "#\n#", "#").concat("\n" + (1 to 12).map(_ => "# ").mkString)

  }
}
case class SimpleBoard(values: Vector[Int]) extends Board

object Board {

  type HeuristicValue = Double

  val RAW_HEIGHT = 24
  val RAW_WIDTH = 12
  val HEIGHT = 22
  val WIDTH = 10
  val WALL = 9
  val EMPTY = 0

  val DEFAULT_COLUMN = 4

  trait BoardParser {

    def parseBoard(serializedBoard: String): RawBoard = {

      val boardData = serializedBoard.map(_.asDigit) // convert chars to digits
        .grouped(RAW_HEIGHT).toVector// group by columns
        .drop(1).dropRight(1) // remove first and last column, corresponding with walls
        .map(_.dropRight(2).toVector) // remove the floor, also represented as wall

      RawBoard(boardData)
    }
  }

  def simpleProjection(b: RawBoard): SimpleBoard = {
    val values = b.values.map(columnHeight)
    SimpleBoard(values)
  }

  def filterBelowColumns(board: RawBoard, figure: Figure, a: Action): Vector[Vector[Int]] = {
    val colCoords = Figure.getColumnCoords(figure, a)
    board.values.slice(colCoords.min, colCoords.max + 1)
  }

  def columnHeight: Vector[Int] => Int = _.dropWhile(_ == EMPTY).length

  def heightDifferences: RawBoard => Int = b => {
    val h @ _ :: rest = b.values.map(columnHeight).toList
    h.zip(rest).map { case (a, b) => Math.abs(a - b) }.sum
  }

  def averageHeight: RawBoard => Double = b => b.values.map(columnHeight).sum.toDouble / b.values.length

  def numberOfHoles: RawBoard => Int = _.values.flatMap(_.dropWhile(_ == EMPTY)).count(_ == EMPTY)

  def completedRows: RawBoard => Int = _.values.transpose.count(_.forall(_ != EMPTY))

  def computeNextBoard(b: RawBoard, f: Figure, a: Action): RawBoard = {
    val currentFigure = Figure.rotation(f, a.rotation)
    val x = DEFAULT_COLUMN + a.movement.value

    val heights = b.values.map(columnHeight)
    val belowColHeights = filterBelowColumns(b, currentFigure, a).map(columnHeight)

    def grounded(b: RawBoard, f: Figure, r: Rotation, x: Int, y: Int): Boolean = {
      val figureHeights = Figure.computeBelowHeights(f, r, y)
      belowColHeights.zip(figureHeights).exists{case (bh, fh) => bh == fh}
    }

    var y = heights.max + 1
    while(!grounded(b, f, a.rotation, x, y) && y > 0) {
      y = y - 1
    }

    insertFigure(b, f, a.rotation, x, y)
  }

  // TODO: test
  def insertFigure(b: RawBoard, f: Figure, r: Rotation, x: Int, y: Int): RawBoard = {
    setValues(b, f.symbol, Figure.getCoordinates(f, r, x, y))
  }

  // TODO: test
  def setValues(b: RawBoard, fId: Int, coords: List[(Int, Int)]): RawBoard = {
    coords.foldLeft(b) { case (b, (x, y)) =>
        RawBoard(b.values.updated(x, b.values(x).updated(HEIGHT - 1 - y, fId)))
    }
  }

  def heuristicEval(b: RawBoard): HeuristicValue = {
    val holes = Board.numberOfHoles(b)
    val heightDiff = Board.heightDifferences(b)
    val aggregateHeight = Board.averageHeight(b)
    val completedRows = Board.completedRows(b)

    // Values taken from reference article linked in README
    -0.51d * aggregateHeight + 0.76d * completedRows + -0.36d * holes + -0.18d * heightDiff
  }
}
