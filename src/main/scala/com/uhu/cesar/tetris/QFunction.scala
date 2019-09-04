package com.uhu.cesar.tetris

import java.io._

import com.uhu.cesar.tetris.Action.{Movement, Rotation}
import com.uhu.cesar.tetris.Board.SimpleBoard
import com.uhu.cesar.tetris.Figure.FigureSymbol
import com.uhu.cesar.tetris.QFunction.{QFunctionKey, QFunctionValue}

import scala.collection.immutable.HashMap
import scala.util.Random

case class QFunction(data: HashMap[QFunctionKey, QFunctionValue]) {

  def get(k: QFunctionKey): QFunctionValue = data.getOrElse(k, Random.nextDouble * 800)

  def update(k: QFunctionKey, v: QFunctionValue): QFunction = QFunction(data.updated(k, v))

  def bestActionValue(board: Board, figure: Figure): QFunctionValue = {
    board.computeNextBoard(figure).map(board => get(board.simpleProjection)).max
  }

  def bestActionValue(board: Board, figure: Figure, nextFigure: Figure): QFunctionValue = {
    board.computeNextBoard(figure)
      .flatMap(_.computeNextBoard(figure))
      .map(board => get(board.simpleProjection))
      .max
  }

}

object QFunction {

  private val DEFAULT_VALUE: QFunctionValue = 0d

  type QFunctionKey = SimpleBoard
  type QFunctionValue = Double

  val name = "default-qfunction"

  trait QFunctionSerializer {
    def writeQFunction(qf: QFunction): Unit = {
      println(s"size of qfunction: ${qf.data.size}")
      val out = new ObjectOutputStream(new FileOutputStream(name))
      out.writeObject(qf)
      out.close()
      println(s"Saved QFunction as $name")
    }
  }

  trait QFunctionLoader {
    def loadQFunction(filename: String): QFunction = {

      println(s"loading function $filename")
      def closing[A, B <: Closeable](c: B)(f: B => A): A = try f(c) finally c.close()

      if (new File(filename).exists) {
        closing(new ObjectInputStream(new FileInputStream(name))) { fi =>
          fi.readObject().asInstanceOf[QFunction]
        }
      } else QFunction.empty
    }
  }

  def defaultValue(k: QFunctionKey): QFunctionValue = DEFAULT_VALUE

  def empty: QFunction = QFunction(HashMap.empty[QFunctionKey, QFunctionValue])

}
