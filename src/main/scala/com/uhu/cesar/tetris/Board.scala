package com.uhu.cesar.tetris

import Player.{Action, Rotation}

sealed trait Board extends Product with Serializable
case class RawBoard(values: Vector[Vector[Int]]) extends Board
case class SimpleBoard(values: Vector[Int]) extends Board

object Board {

  type HeuristicValue = Float

  val HEIGHT = 24
  val WIDTH = 10
  val WALL = 9
  val EMPTY = 0

  val DEFAULT_COLUMN = 4

  trait BoardParser {

    def parseBoard(serializedBoard: String): RawBoard = {

      val boardData = serializedBoard.map(_.asDigit) // convert chars to digits
        .grouped(HEIGHT).toVector// group by columns
        .drop(1).dropRight(1) // remove first and last column, corresponding with walls
        .map(_.dropRight(2).toVector) // remove the floor, also represented as wall

      RawBoard(boardData)
    }
  }

  // TODO: test
  def simpleProjection(b: RawBoard): SimpleBoard = {
    val values = b.values.map(columnHeight)
    SimpleBoard(values)
  }

  // TODO: test
  def filterBelowColumns(board: RawBoard, figure: Figure, move: Int): Vector[Vector[Int]] = {
    val start = DEFAULT_COLUMN - move
    val end = start + figure.width
    board.values.slice(start, end)
  }

  // TODO: test
  def columnHeight: Vector[Int] => Int = _.dropWhile(_ == EMPTY).length

  // TODO: test
  def heightDifferences: RawBoard => Int = b => {
    val h @ _ :: rest = b.values.map(columnHeight).toList
    h.zip(rest).map { case (a, b) => Math.abs(a - b) }.sum
  }

  // TODO: test
  def aggregateHeight: RawBoard => Int = _.values.map(columnHeight).sum

  // TODO: test
  def numberOfHoles: RawBoard => Int = _.values.flatMap(_.dropWhile(_ == EMPTY)).count(_ == EMPTY)

  // TODO: test
  def completedRows: RawBoard => Int = _.values.transpose.count(l => !l.contains(EMPTY))

  // TODO: test
  def computeNextBoard(b: RawBoard, f: Figure, a: Action): RawBoard = {
    val currentFigure = Figure.rotation(f, a.rotation)
    val x = DEFAULT_COLUMN + a.movement.value

    val heights = b.values.map(columnHeight)
    val belowColHeights = filterBelowColumns(b, currentFigure, x - DEFAULT_COLUMN).map(columnHeight)

    def grounded(b: RawBoard, f: Figure, r: Rotation, x: Int, y: Int): Boolean = {
      val figureHeights = Figure.computeBelowHeights(f, r, y)

      belowColHeights.zip(figureHeights).exists{case (bh, fh) => bh + 1 == fh}
    }

    var y = heights.max
    while(!grounded(b, f, a.rotation, x, y)) {
      y = y - 1
    }

    insertFigure(b, f, a.rotation, y, x)
  }

  // TODO: test
  def insertFigure(b: RawBoard, f: Figure, r: Rotation, x: Int, y: Int): RawBoard = {
    setValues(b, f.symbol, Figure.getCoordinates(f, r, x, y))
  }

  // TODO: test
  def setValues(b: RawBoard, fId: Int, coords: List[(Int, Int)]): RawBoard = {
    coords.foldLeft(b) { case (b, (x, y)) =>
        RawBoard(b.values.updated(y, b.values(y).updated(x, fId)))
    }
  }

  def heuristicEval(b: RawBoard): HeuristicValue = {
    val holes = Board.numberOfHoles(b)
    val heightDiff = Board.heightDifferences(b)
    val aggregateHeight = Board.aggregateHeight(b)
    val completedRows = Board.completedRows(b)

    // Values taken from reference article linked in README
    -0.51f * aggregateHeight + 0.76f * completedRows + -0.36f * holes + -0.18f * heightDiff
  }

}
