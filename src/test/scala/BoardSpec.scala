import com.uhu.cesar.tetris.Board.{BoardParser, SimpleBoard}
import com.uhu.cesar.tetris.{Action, Board, Figure}
import org.scalatest.{FlatSpec, Matchers}

class BoardSpec extends FlatSpec with Matchers with BoardParser {

  val board1: Board = Board.emptyBoard.computeNextBoard(Figure(3), Action(4, 0))
  val board2: Board = board1.computeNextBoard(Figure(3), Action(3, 0))
  val boardCompletedLine: Board = board1.computeNextBoard(List(
    (Figure(0), Action(-4, 0)),
    (Figure(0), Action(0, 0)))
  )

  "A board" should "have correct height differences" in {
    Board.emptyBoard.heightDifferences shouldEqual 0
    board1.heightDifferences shouldEqual 2
    board2.heightDifferences shouldEqual 6
  }

  it should "compute average height" in {
    Board.emptyBoard.averageHeight shouldEqual 0d
    board1.averageHeight shouldEqual 0.4d
  }

  it should "compute the correct number of holes as all empty spaces below the max height of every column" in {
    Board.emptyBoard.numberOfHoles shouldEqual 0
    board1.numberOfHoles shouldEqual 0
    board2.numberOfHoles shouldEqual 2
  }

  // Extend this test using figures with differences in height and width
  it should "slice using a figure" in {
    val figure = Figure.SQUARE
    val slice = board1.filterBelowColumns(figure, Action(3, 0))
    val expected = Vector.tabulate[Int](2, Board.HEIGHT) { (x, y) => if (x == 1 && y >= 20) 3 else 0 }
    slice shouldEqual expected
  }

  it should "compute correct column heights" in {
    board1.values.map(Board.columnHeight) shouldEqual Vector(0, 0, 0, 0, 0, 0, 0, 0, 2, 2)
  }

  it should "compute the number of completed rows" in {
    board1.completedRows shouldEqual 0
    boardCompletedLine.completedRows shouldEqual 1
  }

  it should "compute the next board, given the current board, a figure and an action" in {
    val figure = Figure.SQUARE
    val expectedBoard = parseBoard("999999999999999999999990000000000000000000003390000000000000000000003390000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000003390000000000000000000003390999999999999999999999990")
    val expectedBoard2 = parseBoard("999999999999999999999990000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000330090000000000000000000333390000000000000000000003390999999999999999999999990")

    board1.computeNextBoard(figure, Action(-4, 0)).values shouldEqual expectedBoard.values
    board1.computeNextBoard(figure, Action(3, 0)).values shouldEqual expectedBoard2.values
  }

  it should "have better heuristic value if has completed rows" in {
    def fillWithSquares(a: List[(Int, Int)]): Board ={
      val actionList = a.map{ case (m, r) => (Figure.SQUARE, Action(m, r)) }
      Board.emptyBoard.computeNextBoard(actionList)
    }

    val twoRows = fillWithSquares(List((-4, 0), (-2, 0), (0, 0), (2, 0), (4, 0)))
    val almostTwoRows = fillWithSquares(List((-4, 0), (-2, 0), (0, 0), (2, 0)))
    val worst = fillWithSquares(List((-4, 0), (-2, 0), (0, 0), (2, 0), (-4, 0)))

    twoRows.heuristicEval > almostTwoRows.heuristicEval shouldEqual true
    twoRows.heuristicEval > worst.heuristicEval shouldEqual true
    almostTwoRows.heuristicEval > worst.heuristicEval shouldEqual true
  }

  it should "declare ilegal a move that leaves a piece higher that the max height" in {

  }

}
