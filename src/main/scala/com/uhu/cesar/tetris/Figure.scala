package com.uhu.cesar.tetris

import Player.{Action, Rotation}

case class Figure(symbol: Int, width: Int, moves: Array[Int], grid: Array[Array[Int]])

// TODO: Load figures from file
object Figure {
  val SQUARE = Figure(
    symbol = 7,
    width = 2,
    moves = (-4 to 4).toArray,//.filter(_ % 2 == 0),
    grid = Array(
      Array(7, 7),
      Array(7, 7),
    )
  )

  val EMPTY = Figure(
    symbol = 0,
    width = 0,
    moves = Array.empty,
    grid = Array.ofDim(2)
  )

  def isFigure(symbol: Int): Boolean = symbol == 3

  // TODO: test
  def rotation(f: Figure, r: Rotation): Figure = {

    // Apply n times the function f, which gives an A from another A,
    // so f function can be applied indefinitely
    def applyNTimes[A](n: Int)(f: A => A): A => A = (obj: A) => (1 to n).foldLeft(obj)( (dObj, _) => f(dObj) )

    // Rotate 90º clockwise by applying two symmetries.
    // 1º main diagonal symmetry ( transpose ). 2º vertical symmetry ( map(_.reverse) )
    val rotate90: Figure => Figure = (obj: Figure) => obj.copy(grid = obj.grid.transpose.map(_.reverse))

    // Apply that N times
    val rotate: Figure => Figure = applyNTimes(r.value)(rotate90)


    rotate(f)
  }


  def apply(symbol: Int): Figure = symbol match {
    case SQUARE.symbol => SQUARE
  }

  // TODO: test
  def getCoordinates(f: Figure, r: Rotation, x: Int, y: Int): List[(Int, Int)] = {

    val columns = getFigureAsColumns(f, r).grid
    val columnCoords = columns.indices.map{ dx => (x + dx, y) }.toList

    columns.zip(columnCoords).flatMap { case (col, (x, y)) =>
        col.zipWithIndex.filter(_._1 != 0).map { case (_, dy) => (x, y + dy) }
    }.toList
  }

  def getFigureAsColumns(f: Figure, r: Rotation): Figure = {
    f.copy(grid = rotation(f, r).grid.transpose)
  }

  def getColumnCoords(f: Figure, a: Action): List[Int] = {
    rotation(f, a.rotation)
      .grid(0).indices
      .map(_ + Board.DEFAULT_COLUMN + a.movement.value).toList
  }

  // TODO: test
  def computeBelowHeights(f: Figure, r: Rotation, y: Int): List[Int] = {
    rotation(f, r).grid.transpose.map( col => y + col.length - col.reverse.dropWhile(_ == 0).length ).toList
  }

}
