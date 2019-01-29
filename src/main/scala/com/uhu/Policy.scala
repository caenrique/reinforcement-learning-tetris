package com.uhu

import com.uhu.Player.Action

import scala.util.Random

object Policy {

  type Policy = Seq[(Action, Float)] => Action

  def onPolicy: Policy = actions => {
    val choice = Random.shuffle(actions).maxBy(_._2)._1
    choice
  }

  def randomPolicy: Policy = actions => {
    val choice = Random.shuffle(actions).headOption.map(_._1).getOrElse(0 -> 0)
    choice
  }

  def eGreedy: Policy = if (Random.nextFloat() > 0.2) onPolicy else randomPolicy

}
