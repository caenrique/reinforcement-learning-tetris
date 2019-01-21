import com.uhu.Board.BoardParser
import com.uhu._
import org.scalatest.{FlatSpec, Matchers}

class MessageSpec extends FlatSpec with Matchers {

  "A message" should "be separated by ;" in {
    val correctMovMessage = "MOV;4;2;0000000;100;3"
    val correctEndMessage = "FIN;user;name;1000"
    val badMessage = "ohteruser;name;1000"

    trait DummyBoardParser extends BoardParser {
      override def parseBoard(serializedBoard: String): Board = Board(Array.ofDim[Int](Board.WIDTH, Board.HEIGHT))
    }

    object DummyMessageParser extends Message.MessageParser with DummyBoardParser

    DummyMessageParser.parseMessage(correctMovMessage) shouldBe a[MovMessage]
    DummyMessageParser.parseMessage(correctEndMessage) shouldBe a[EndMessage]
    DummyMessageParser.parseMessage(badMessage) shouldBe BadMessage
  }

}
