package akka.tutorials.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.testkit.TestKit
import akka.testkit.TestKit._
import akka.util.duration._
import akka.tutorials.conway._

class BoardSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
    //tests to add
    //verify that the board to be renedered is correct
    //verify that only when boards are complete that a message is to displayRef is sent
    val cannedBoardState = Array.ofDim[Boolean](1, 1) 
    cannedBoardState(0)(0) = true
    
    val threeByThreeBoard = Array.ofDim[Boolean](3,3)
    
    "A BoardActor" should {

      "try to render a board if the BoardToControllerDisplayRound returns true" in {
        
        val controllerStub = actorOf(new ControllerStub(testActor, true)).start()        
        val boardActor = actorOf(new Board(1, 1, testActor, controllerStub)).start()
        
        within (2000 millis) {
          boardActor !  CellToBoard(true, 0 , 0, 0)
          expectMsg(BoardState(0,cannedBoardState))
        }
      }
      
      "not try to render a board if the BoardToControllerDisplayRound returns false" in {
        
        val controllerStub = actorOf(new ControllerStub(testActor, false)).start()
        val boardActor = actorOf(new Board(1, 1, testActor, controllerStub)).start()
        
        within (2000 millis) {
          boardActor !  CellToBoard(true, 0 , 0, 0)
          expectNoMsg
        }
      }
      
      "send a message to the display actor if the board is complete." in {
        val controllerStub = actorOf(new ControllerStub(testActor, true)).start()
        val boardActor = actorOf(new Board(3, 3, testActor, controllerStub)).start()
        
        within (3000 millis) {
          for (i <- 1 to 9) {
            val response = boardActor ! CellToBoard(false, 0, 0, 0)
          }
          expectMsg(BoardState(0, threeByThreeBoard))
          
        }
      }
    }    
}

class ControllerStub(testActor:ActorRef, returnValue: Boolean) extends Actor {
  def receive = {
    case _ => self.reply(returnValue)
  }
}
