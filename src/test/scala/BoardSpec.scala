package akka.tutorial.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.testkit.TestKit
import akka.testkit.TestKit._
import akka.util.duration._
import akka.tutorials.conway._

class BoardSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {

    val cannedBoardState = Array.ofDim[Boolean](1, 1) 
    cannedBoardState(0)(0) = true

    "A BoardActor must" should {
      "send back an empty board" in {
        val boardActor = actorOf(new Board(1, 1, testActor)).start()
        
        within (1000 millis) {
          boardActor !  CellToBoard(true, 0 , 0, 0)
          expectMsg(BoardState(0,cannedBoardState))
        }
      }
    
    }
}
