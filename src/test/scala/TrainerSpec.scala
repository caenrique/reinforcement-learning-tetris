import com.uhu.cesar.tetris.Action.{Movement, Rotation}
import com.uhu.cesar.tetris.QFunction.QFunctionKey
import com.uhu.cesar.tetris.{Action, Board, Figure, Policy, QFunction, Trainer}
import org.scalatest.{FlatSpec, Matchers}

class TrainerSpec extends FlatSpec with Matchers {

  "A Trainer" should "compute new qvalue given a move, a reward and a potential" in {
    val nextBoard = Board.emptyBoard.computeNextBoard(Figure(3), Action(-4, 0))
    val key: QFunctionKey = (Board.simpleEmptyBoard, 3, Movement(-4), Rotation(0))
    val nextKey: QFunctionKey = (nextBoard.simpleProjection, 3, Movement(4), Rotation(0))
    val qfunction = QFunction(Map(key -> 1.0, nextKey -> 100))
    val trainer = Trainer(0.1f, 0.9f, qfunction, None)(_ => Policy.eGreedy)

    val qvalue0 = trainer.qf.get(key)
    println(s"qValue: $qvalue0")

    val qvalue1 = trainer.computeNewQValue(0, qfunction.bestActionValue(nextBoard.simpleProjection, Figure.SQUARE), key)
    println(s"qValue: $qvalue1")

    val qvalue2 = trainer.computeNewQValue(100, qfunction.bestActionValue(Board.simpleEmptyBoard, Figure.SQUARE), key)
    println(s"qValue: $qvalue2")
  }
}
