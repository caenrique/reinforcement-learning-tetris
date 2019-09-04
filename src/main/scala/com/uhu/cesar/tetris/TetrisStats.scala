package com.uhu.cesar.tetris

trait TetrisStats {

  private var moves = 0
  private var games = 0
  private var lines = 0
  private var averageMovesPerGame: Double = 0
  private var averageLinesPerGame: Double = 0

  def get_averageMovesPerGame(): Double = averageMovesPerGame
  def get_averageLinesPerGame(): Double = averageLinesPerGame

  def recordMove(): Unit = {
    moves = moves + 1
  }

  def recordLine(clearedLines: Int): Unit = {
    lines = lines + clearedLines
  }

  def recordEndOfGame(): Unit = {
    games = games + 1
    averageMovesPerGame = averageMovesPerGame + ((moves - averageMovesPerGame)/games)
    averageLinesPerGame = averageLinesPerGame + ((lines - averageLinesPerGame)/games)
    moves = 0
    lines = 0
  }

}
