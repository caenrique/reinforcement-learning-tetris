package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.Rotation

sealed trait Board extends Product with Serializable

object Board {

  case class RawBoard(values: Vector[Vector[Int]]) extends Board {

    override def toString: String = {
      values.transpose
        .map(_.map(n => if (n == 0) " " else n.toString).mkString(" ", " ", " "))
        .mkString("#", "#\n#", "#").concat("\n" + (1 to 12).map(_ => "# ").mkString)
    }

  }

  case class SimpleBoard(values: Vector[Int]) extends Board

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
        .grouped(RAW_HEIGHT).toVector // group by columns
        .drop(1).dropRight(1) // remove first and last column, corresponding with walls
        .map(_.dropRight(2).toVector) // remove the floor, also represented as wall

      RawBoard(boardData)
    }
  }

  def simpleProjection(b: RawBoard): SimpleBoard = {
    val values = b.values.map(columnHeight)
    SimpleBoard(values)
  }

  def illegalMove(b: RawBoard, f: Figure, a: Action): Boolean = {
    filterBelowColumns(b, f, a).map(columnHeight)
      .exists(ch => ch + Figure.relativeHeight(f, a.rotation) > Board.HEIGHT)
  }

  def filterBelowColumns(board: RawBoard, figure: Figure, a: Action): Vector[Vector[Int]] = {
    val colCoords = Figure.getColumnCoords(figure, a)
    board.values.slice(colCoords.min, colCoords.max + 1)
  }

  def columnTopCoord: Vector[Int] => Int = _.takeWhile(_ == EMPTY).length

  def columnHeight: Vector[Int] => Int = _.dropWhile(_ == EMPTY).length

  def heightDifferences: RawBoard => Int = b => {
    val h@_ :: rest = b.values.map(columnHeight).toList
    h.zip(rest).map { case (a, b) => Math.abs(a - b) }.sum
  }

  def averageHeight: RawBoard => Double = b => b.values.map(columnHeight).sum.toDouble / b.values.length

  def numberOfHoles: RawBoard => Int = _.values.map(_.dropWhile(_ == EMPTY).count(_ == EMPTY)).sum

  def completedRows: RawBoard => Int = _.values.transpose.count(_.forall(_ != EMPTY))

  def computeNextBoard(b: RawBoard, f: Figure, a: Action): RawBoard = {
    val x = DEFAULT_COLUMN + a.movement.value

    val heights = b.values.map(columnTopCoord)

    def grounded(b: RawBoard, f: Figure, r: Rotation, x: Int, y: Int): Boolean = {
      f.coordinates(r.value).exists { case (dx, dy) =>
          if (dy + y == 21) true else b.values(dx + x)(dy + y + 1) != EMPTY
      }
    }

    var y = heights.min - Figure.relativeHeight(f, a.rotation)
    while (!grounded(b, f, a.rotation, x, y) && y < Board.HEIGHT) {
      y = y + 1
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
      RawBoard(b.values.updated(x, b.values(x).updated(y, if (fId == 0) 7 else fId)))
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
