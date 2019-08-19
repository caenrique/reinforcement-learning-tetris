package com.uhu.cesar.tetris

import java.io._

import Player.Action
import QFunction.{QFunctionKey, QFunctionValue}

import scala.util.Random

case class QFunction(data: Map[QFunctionKey, QFunctionValue]) {

  def get(k: QFunctionKey): QFunctionValue = data.getOrElse(k, Random.nextFloat)

  def update(k: QFunctionKey, v: QFunctionValue): QFunction = QFunction(data.updated(k, v))

  def getAll(condition: QFunctionKey => Boolean): Seq[(QFunctionKey, QFunctionValue)] = {
    data.filter { case (k, _) => condition(k) }.toSeq
  }

  def bestActionValue(board: SimpleBoard): QFunctionValue = {
    getAll{ case QFunctionKey(b, _, _) => b == board }
      .map(_._2)
      .reduceOption(_ max _)
      .getOrElse(QFunction.DEFAULT_VALUE)
  }

}

object QFunction {

  private val DEFAULT_VALUE = 0f

  case class QFunctionKey(board: SimpleBoard, figure: Figure, action: Action)
  type QFunctionValue = Float

  // TODO: get this file name from config
  val name = "qfunction"

  trait QFunctionSerializer {
    def write(qf: QFunction): Unit = {
      val out = new ObjectOutputStream(new FileOutputStream(name))
      out.writeObject(qf)
      out.close()
    }
  }

  trait QFunctionLoader {
    def load(filename: String): QFunction = {

      def closing[A, B <: Closeable](c: B)(f: B => A): A = try f(c) finally c.close()

      closing(new ObjectInputStream(new FileInputStream(name))) { fi =>
        fi.readObject().asInstanceOf[QFunction]
      }
    }
  }

  def defaultValue(k: QFunctionKey): QFunctionValue = DEFAULT_VALUE

  def empty: QFunction = QFunction(Map.empty[QFunctionKey, QFunctionValue])

}
