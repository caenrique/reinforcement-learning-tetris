import com.uhu.cesar.tetris.Board.{BoardParser, RawBoard, SimpleBoard}
import com.uhu.cesar.tetris.{Action, Board, Figure}
import org.scalatest.{FlatSpec, Matchers}

class RawBoardSpec extends FlatSpec with Matchers with BoardParser {

  val emptyboard = "999999999999999999999990000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090999999999999999999999990"
  val board1 = "999999999999999999999990000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000003390000000000000000000003390999999999999999999999990"
  val board2 = "999999999999999999999990000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000330090000000000000000000333390000000000000000000003390999999999999999999999990"
  val boardWithHoles = "999999999999999999999990000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000330090000000000000000000333390000000000000000000003390999999999999999999999990"
  val boardCompletedLine = "999999999999999999999990000000000000000000000390000000000000000000000390000000000000000000000390000000000000000000000390000000000000000000000390000000000000000000000390000000000000000000000390000000000000000000333390000000000000000000333390000000000000000000003390999999999999999999999990"

  "A board" should "have correct height differences" in {

    Board.heightDifferences(parseBoard(board1)) shouldEqual 2
    Board.heightDifferences(parseBoard(emptyboard)) shouldEqual 0
    Board.heightDifferences(parseBoard(board2)) shouldEqual 6

  }

  it should "compute average height" in {
    Board.averageHeight(parseBoard(emptyboard)) shouldEqual 0d
    Board.averageHeight(parseBoard(board1)) shouldEqual 0.4d
  }

  it should "compute the correct number of holes as all empty spaces below the max height of every column" in {
    Board.numberOfHoles(parseBoard(emptyboard)) shouldEqual 0
    Board.numberOfHoles(parseBoard(board2)) shouldEqual 2
  }

  it should "be convertible from RawBoard to SimpleBoard" in {
    Board.simpleProjection(parseBoard(board1)) shouldEqual SimpleBoard(Vector(0, 0, 0, 0, 0, 0, 0, 0, 2, 2))
    Board.simpleProjection(parseBoard(boardWithHoles)) shouldEqual SimpleBoard(Vector(0, 0, 0, 0, 0, 0, 0, 4, 4, 2))
  }

  // Extend this test using figures with differences in height and width
  it should "slice using a figure" in {
    val figure = Figure.SQUARE
    val slice = Board.filterBelowColumns(parseBoard(board1), figure, Action(3, 0))
    slice shouldEqual Vector(
      Vector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      Vector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3)
    )
  }

  it should "compute correct column heights" in {
    parseBoard(board1).values.map(Board.columnHeight) shouldEqual Vector(0, 0, 0, 0, 0, 0, 0, 0, 2, 2)
  }

  it should "compute the number of completed rows" in {
    Board.completedRows(parseBoard(board1)) shouldEqual 0
    Board.completedRows(parseBoard(boardCompletedLine)) shouldEqual 1
  }

  it should "compute the next board, given the current board, a figure and an action" in {
    val figure = Figure.SQUARE
    val board = parseBoard(board1)
    val expectedBoard = parseBoard("999999999999999999999990000000000000000000003390000000000000000000003390000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000003390000000000000000000003390999999999999999999999990")
    val expectedBoard2 = parseBoard("999999999999999999999990000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000000090000000000000000000330090000000000000000000333390000000000000000000003390999999999999999999999990")

    Board.computeNextBoard(board, figure, Action(-4, 0)).values shouldEqual expectedBoard.values
    Board.computeNextBoard(board, figure, Action(3, 0)).values shouldEqual expectedBoard2.values
  }

  it should "have better heuristic value if has completed rows" in {
    def fillWithSquares(a: List[(Int, Int)]): RawBoard = a.foldLeft(parseBoard(emptyboard)) { case (b, (d, r)) =>
      Board.computeNextBoard(b, Figure.SQUARE, Action(d, r))
    }

    val twoRows = List((-4, 0), (-2, 0), (0, 0), (2, 0), (4, 0))
    val almostTwoRows = List((-4, 0), (-2, 0), (0, 0), (2, 0))
    val worst = List((-4, 0), (-2, 0), (0, 0), (2, 0), (-4, 0))

    val bestBoard = Board.heuristicEval(fillWithSquares(twoRows))
    val normalBoard = Board.heuristicEval(fillWithSquares(almostTwoRows))
    val worstBoard = Board.heuristicEval(fillWithSquares(worst))

    //println(s"best: $bestBoard, normal: $normalBoard, worst: $worstBoard")

    bestBoard > normalBoard shouldEqual true
    bestBoard > worstBoard shouldEqual true
    normalBoard > worstBoard shouldEqual true

  }

  it should "declare ilegal a move that leaves a piece higher that the max height" in {

  }

}
