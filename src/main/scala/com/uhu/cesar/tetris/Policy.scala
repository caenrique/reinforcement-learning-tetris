package com.uhu.cesar.tetris

import com.uhu.cesar.tetris.Board.HeuristicValue
import com.uhu.cesar.tetris.QFunction.QFunctionValue

import scala.util.Random

object Policy {

  type Policy = Seq[(Action, QFunctionValue, HeuristicValue)] => Action

  // Sort by QFunction value and get the first action
  def onPolicy: Policy = actions => {
    val choice = Random.shuffle(actions).maxBy(_._2)._1
    choice
  }

  // Random action from posible ones. If there are no actions, do nothing, aka Action(0, 0)
  def randomPolicy: Policy = actions => {
    Random.shuffle(actions).headOption.map(_._1).getOrElse(Action(0, 0))
  }

  // Sort by Heuristics value and get the first action
  def heuristicPolicy: Policy = actions => {
    Random.shuffle(actions).maxBy(_._3)._1
  }

  def eGreedy(epsilon: Double)(episode: Int): Policy = {
    if (Random.nextFloat() > epsilon) onPolicy else randomPolicy
  }

  def heuristicEGreedy(epsilon: Double)(episode: Int): Policy = {
    val r = Random.nextFloat()
    if (r < epsilon) heuristicPolicy
    else onPolicy

    heuristicPolicy

  }

}
