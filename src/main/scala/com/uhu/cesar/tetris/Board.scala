package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.Rotation
import com.uhu.cesar.tetris.Board._
import com.uhu.cesar.tetris.Figure.FigureSymbol

case class Board(values: Vector[Vector[Int]]) {

  def simpleProjection: SimpleBoard = SimpleBoard(values.map(columnHeight))

  def averageHeight: Double = values.map(columnHeight).sum.toDouble / values.length

  def numberOfHoles: Int = values.map(_.dropWhile(_ == EMPTY).count(_ == EMPTY)).sum

  def completedRows: Int = values.transpose.count(_.forall(_ != EMPTY))

  def heightDifferences: Int = {
    val h@_ :: rest = values.map(columnHeight).toList
    h.zip(rest).map { case (a, b) => Math.abs(a - b) }.sum
  }

  def computeNextBoard(actions: List[(Figure, Action)]): Board = {
    actions.foldLeft(this) { case (b, (f, a)) => b.computeNextBoard(f, a) }
  }

  def computeNextBoard(figure: Figure, action: Action): Board = {
    val x = DEFAULT_COLUMN + action.movement.value

    val heights = filterBelowColumns(figure, action).map(columnTopCoord)

    var y = heights.min - figure.relativeHeight(action.rotation)
    while (!grounded(figure, action.rotation, x, y) && y < HEIGHT) {
      y = y + 1
    }

    insertFigure(figure, action.rotation, x, y)
  }

  def illegalMove(figure: Figure, action: Action): Boolean = {
    filterBelowColumns(figure, action).map(columnHeight)
      .exists(ch => ch + figure.relativeHeight(action.rotation) > HEIGHT)
  }

  def filterBelowColumns(figure: Figure, action: Action): Vector[Vector[Int]] = {
    val colCoords = figure.getColumnCoords(action)
    values.slice(colCoords.min, colCoords.max + 1)
  }

  def grounded(f: Figure, r: Rotation, x: Int, y: Int): Boolean = {
    f.coordinates(r.value).exists { case (dx, dy) =>
      if (dy + y == 21) true else values(dx + x)(dy + y + 1) != EMPTY
    }
  }

  // TODO: test
  def insertFigure(figure: Figure, rotation: Rotation, x: Int, y: Int): Board = {
    setValues(figure.symbol, figure.getCoordinates(rotation, x, y))
  }

  // TODO: test
  def setValues(figureSymbol: Int, coords: List[(Int, Int)]): Board = {
    coords.foldLeft(this) { case (b, (x, y)) => b.setValue(figureSymbol, x, y) }
  }

  def setValue(figureSymbol: FigureSymbol, x: Int, y: Int): Board = {
    Board(values.updated(x, values(x).updated(y, if (figureSymbol == 0) 7 else figureSymbol)))
  }

  def heuristicEval: HeuristicValue = {
    // Values taken from reference article linked in README
    -0.51d * averageHeight + 0.76d * completedRows + -0.36d * numberOfHoles + -0.18d * heightDifferences
  }

  override def toString: String = {
    values.transpose
      .map(_.map(n => if (n == 0) " " else n.toString).mkString(" ", " ", " "))
      .mkString("#", "#\n#", "#").concat("\n" + (1 to 12).map(_ => "# ").mkString)
  }

}

object Board {

  type HeuristicValue = Double

  case class SimpleBoard(values: Vector[Int])

  val RAW_HEIGHT = 24
  val RAW_WIDTH = 12
  val HEIGHT = 22
  val WIDTH = 10
  val WALL = 9
  val EMPTY = 0
  val DEFAULT_COLUMN = 4

  val emptyBoard: Board = Board(Vector.tabulate(WIDTH, HEIGHT)((_, _) => 0))
  val simpleEmptyBoard: SimpleBoard = emptyBoard.simpleProjection

  def columnTopCoord: Vector[Int] => Int = _.takeWhile(_ == EMPTY).length

  def columnHeight: Vector[Int] => Int = _.dropWhile(_ == EMPTY).length

  trait BoardParser {

    def parseBoard(serializedBoard: String): Board = {

      val boardData = serializedBoard.map(_.asDigit) // convert chars to digits
        .grouped(RAW_HEIGHT).toVector // group by columns
        .drop(1).dropRight(1) // remove first and last column, corresponding with walls
        .map(_.dropRight(2).toVector) // remove the floor, also represented as wall

      Board(boardData)
    }
  }

}
