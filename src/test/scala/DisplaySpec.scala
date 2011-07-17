package akka.tutorials.conway

import akka.testkit.TestKit
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import akka.util.duration._
import akka.actor.Actor._

class DisplaySpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {

  val xSize = 5
  val ySize = 5

  val boardState = Array.ofDim[Boolean](xSize, ySize)

  boardState(2)(2) = true
  boardState(3)(2) = true
  boardState(3)(3) = true
  boardState(2)(3) = true

  "An ASCIIDisplay " should {
    " should print out a boardState " in {
      val displayActor = actorOf(new ASCIIDisplay()).start
      within (1000 millis) {
        displayActor ! BoardState(0, boardState)
      }
    }
  }

}