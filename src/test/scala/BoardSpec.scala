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
    
    private def createBoard(xSize: Int, ySize: Int,  alive: Boolean): Array[Array[Boolean]] = {
      val board = Array.ofDim[Boolean](xSize, ySize)
      for(i <- 0 until xSize) {
        for(j <- 0 until ySize ) {
          board(i)(j) = alive
        }
      }
      return board
    }
    
    "A BoardActor" should {

      "try to render a board if the BoardToControllerDisplayRound returns true" in {
        
        val controllerStub = actorOf(new ControllerStub(testActor, true)).start()        
        val boardActor = actorOf(new Board(1, 1, testActor, controllerStub)).start()
        
        within (2000 millis) {
          boardActor !  CellToBoard(true, 0 , 0, 0)
          expectMsg(BoardState(0,createBoard(1,1,true)))
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
          expectMsg(BoardState(0, createBoard(3,3,false)))
          
        }
      }
      "send not message to the display actor if the board is complete." in {
        val controllerStub = actorOf(new ControllerStub(testActor, true)).start()
        val boardActor = actorOf(new Board(3, 3, testActor, controllerStub)).start()
        
        within (3000 millis) {
          for (i <- 1 to 6) {
            val response = boardActor ! CellToBoard(false, 0, 0, 0)
          }
         expectNoMsg
          
        }
      }
      "send a board that shows all cells are alive if every message received indicates that a cell is alive." in {
        val controllerStub = actorOf(new ControllerStub(testActor, true)).start()
        val boardActor = actorOf(new Board(3, 3, testActor, controllerStub)).start()
        val board = createBoard(3,3, true)
        
        within (3000 millis) {
          for (i <- 0 to 2) {
            for(j <- 0 to 2) {  
              val response = boardActor ! CellToBoard(true, 0, i, j)
            }
          }
            
         expectMsg(BoardState(0,board))
        }
      }
    }    
}

class ControllerStub(testActor:ActorRef, returnValue: Boolean) extends Actor {
  def receive = {
    case _ => self.reply(returnValue)
  }
}
