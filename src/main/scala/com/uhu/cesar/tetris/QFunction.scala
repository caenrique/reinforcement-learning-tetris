package com.uhu.cesar.tetris

import java.io._

import com.uhu.cesar.tetris.Action.{Movement, Rotation}
import com.uhu.cesar.tetris.Board.SimpleBoard
import com.uhu.cesar.tetris.Figure.FigureSymbol
import com.uhu.cesar.tetris.QFunction.{QFunctionKey, QFunctionValue}

import scala.collection.immutable.HashMap
import scala.util.Random

case class QFunction(data: HashMap[QFunctionKey, QFunctionValue]) {

  def get(k: QFunctionKey): QFunctionValue = data.getOrElse(k, Random.nextDouble)

  def update(k: QFunctionKey, v: QFunctionValue): QFunction = QFunction(data.updated(k, v))

  def bestActionValue(board: Board, figure: Figure): QFunctionValue = {
    val searchItems = (0 to 3).flatMap(r => figure.moves(Rotation(r)).map { m => (r, m) })
    searchItems.map { case (r, m) =>
      get((board.simpleProjection(figure, Rotation(r)), Movement(m)))
    }.max
  }

}

object QFunction {

  private val DEFAULT_VALUE: QFunctionValue = 0d

  type QFunctionKey = (SimpleBoard, Movement)
  type QFunctionValue = Double

  val name = "default-qfunction"

  def closing[A, B <: Closeable](c: B)(f: B => A): A = try f(c) finally c.close()

  trait QFunctionSerializer {
    def writeQFunction(qf: QFunction, filename: String = name): Unit = {
      closing(new ObjectOutputStream(new FileOutputStream(filename))) { out =>
        out.writeObject(qf)
      }
      println(s"Saved QFunction as $filename")
    }
  }

  trait QFunctionLoader {
    def loadQFunction(filename: String): QFunction = {
      if (new File(filename).exists) {
        println(s"loading function $filename")
        closing(new ObjectInputStream(new FileInputStream(name))) { fi =>
          fi.readObject().asInstanceOf[QFunction]
        }
      } else QFunction.empty
    }
  }

  def defaultValue(k: QFunctionKey): QFunctionValue = DEFAULT_VALUE

  def empty: QFunction = QFunction(HashMap.empty[QFunctionKey, QFunctionValue])

}
