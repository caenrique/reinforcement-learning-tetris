package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Action.Rotation

case class Figure(symbol: Int, coordinates: List[List[(Int, Int)]])

// TODO: Load figures from file
object Figure {

  val STICK = Figure(
    symbol = 0,
    coordinates = List(List((0, 1), (1, 1), (2, 1), (3, 1)), List((1, 0), (1, 1), (1, 2), (1, 3)), List((0, 1), (1, 1), (2, 1), (3, 1)), List((1, 0), (1, 1), (1, 2), (1, 3)))
  )

  val ELE = Figure(
    symbol = 1,
    coordinates = List(List((0, 1), (1, 1), (2, 1), (2, 0)), List((1, 0), (1, 1), (1, 2), (2, 2)), List((0, 1), (1, 1), (2, 1), (0, 2)), List((1, 0), (1, 1), (1, 2), (0, 0)))
  )

  val JOTA = Figure(
    symbol = 2,
    coordinates = List(List((0, 1), (1, 1), (2, 1), (2, 2)), List((1, 0), (1, 1), (1, 2), (0, 2)), List((0, 1), (1, 1), (2, 1), (0, 0)), List((1, 0), (1, 1), (1, 2), (2, 0)))
  )

  val SQUARE = Figure(
    symbol = 3,
    coordinates = List(List((0, 0), (0, 1), (1, 0), (1, 1)), List((0, 0), (0, 1), (1, 0), (1, 1)), List((0, 0), (0, 1), (1, 0), (1, 1)), List((0, 0), (0, 1), (1, 0), (1, 1)))
  )

  val ESE = Figure(
    symbol = 4,
    coordinates = List(List((1, 0), (2, 0), (0, 1), (1, 1)), List((0, 0), (0, 1), (1, 1), (1, 2)), List((1, 0), (2, 0), (0, 1), (1, 1)), List((0, 0), (0, 1), (1, 1), (1, 2)))
  )

  val PIRAMID = Figure(
    symbol = 5,
    coordinates = List(List((1, 0), (0, 1), (1, 1), (2, 1)), List((1, 0), (0, 1), (1, 1), (1, 2)), List((0, 1), (1, 1), (2, 1), (1, 2)), List((1, 0), (1, 1), (2, 1), (1, 2)))
  )

  val ZETA = Figure(
    symbol = 6,
    coordinates = List(List((0, 0), (1, 0), (1, 1), (2, 1)), List((1, 0), (0, 1), (1, 1), (0, 2)), List((0, 0), (1, 0), (1, 1), (2, 1)), List((1, 0), (0, 1), (1, 1), (0, 2)))
  )

  def isFigure(symbol: Int): Boolean = (0 to 6).contains(symbol)

  def apply(symbol: Int): Figure = symbol match {
    case STICK.symbol => STICK
    case JOTA.symbol => JOTA
    case ELE.symbol => ELE
    case SQUARE.symbol => SQUARE
    case ESE.symbol => ESE
    case PIRAMID.symbol => PIRAMID
    case ZETA.symbol => ZETA
    case _ => Figure(-1, List.empty)
  }

  def relativeHeight(f: Figure, r: Rotation): Int = f.coordinates(r.value).maxBy(_._2)._2 + 1

  def moves(f: Figure, r: Rotation): List[Int] = {
    val coords = f.coordinates(r.value)
    val xmin = coords.map(_._1).min
    val xmax = coords.map(_._1).max

    val start = -(Board.DEFAULT_COLUMN + xmin)
    val end = Board.WIDTH - Board.DEFAULT_COLUMN - xmax - 1

    (start to end).toList
  }

  // TODO: test
  def getCoordinates(f: Figure, r: Rotation, x: Int, y: Int): List[(Int, Int)] = {
    f.coordinates(r.value).map{ case (dx, dy) => (x + dx, y + dy) }
  }

  def getColumnCoords(f: Figure, a: Action): List[Int] = {
    f.coordinates(a.rotation.value).map(_._1 + a.movement.value + Board.DEFAULT_COLUMN).distinct
  }

  // TODO: test
  def computeBelowHeights(f: Figure, r: Rotation, y: Int): List[Int] = {
    f.coordinates(r.value)
      .groupBy(_._1).toList
      .sortBy(_._1)
      .map { case (_, coords) => coords.map(_._2).max }
      .map(_ + y)
  }

}
