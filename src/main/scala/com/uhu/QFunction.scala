package com.uhu

import java.io._

import com.uhu.Player.Action
import com.uhu.QFunction.{QFunctionKey, QFunctionValue}

case class QFunction(data: Map[QFunctionKey, QFunctionValue]) {

  def get(k: QFunctionKey): QFunctionValue = data.getOrElse(k, QFunction.defaultValue(k))

  def update(k: QFunctionKey, v: QFunctionValue): QFunction = QFunction(data.updated(k, v))

  def getAll(condition: QFunctionKey => Boolean): Seq[(QFunctionKey, QFunctionValue)] = {
    data.filter { case (k, v) => condition(k) }.toSeq
  }

  def bestActionValue(b: ConditionalBoard): QFunctionValue = {
    getAll(_._1 == b).map(_._2).reduceOption(_ max _).getOrElse(QFunction.DEFAULT_VALUE)
  }

}

object QFunction {

  private val DEFAULT_VALUE = 0f

  type QFunctionKey = (ConditionalBoard, Action)
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
