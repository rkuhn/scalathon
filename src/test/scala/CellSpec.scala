package akka.tutorials.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterEach}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.actor.UnhandledMessageException
import akka.testkit.TestKit
import akka.util.duration._

class CellSpec extends WordSpec with BeforeAndAfterEach with ShouldMatchers with TestKit {
  val controller = actorOf(new ControllerStub(testActor)).start()
  val board = actorOf(new BoardStub(testActor)).start()
  var cell = actorOf(new Cell(0,0,controller,board))

  override protected def afterEach() {
    cell.stop()
  }

  def startCellExpectingRegistration() = {
    cell = actorOf(new Cell(0,0,controller,board)).start()
    expectMsg(('controller, CellRegistration(0,0)))
    cell
  }

  "A Cell" should {
    "Send a registration to the controller" in {
       within(1000 millis) {
         startCellExpectingRegistration() 
      }
    }
    "error upon attempt to start before initialized " in {
      within(1000 millis) {
        evaluating {
          (startCellExpectingRegistration ? ControllerToCellStart).await.get
        } should produce [UnhandledMessageException]
      }
    }
    "error upon attempt to respond to other cells before initialized " in {
      within(1000 millis) {
        evaluating {
          (startCellExpectingRegistration ? ControllerToCellStart).await.get
        } should produce [UnhandledMessageException]
      }
    }
    "notify neighbors of current state on startup" in {
      within(200 millis) {
        cell = startCellExpectingRegistration()
        val neighbors = Array(
          actorOf(new NeighborStub(testActor, 1)).start, 
          actorOf(new NeighborStub(testActor, 2)).start, 
          actorOf(new NeighborStub(testActor, 3)).start)
        cell ! ControllerToCellInitialize(true, neighbors)
        cell ! ControllerToCellStart
        expectMsgAllOf((1 to 3).map(('neighbor, _, CellToCell(true, 0))):_*)
        expectMsg(('board, CellToBoard(true, 0, 0, 0)))
      }
    }
    "notify the board its alive when it receives 3 alive and 5 dead neighbor messages" in {
      within (1000 millis) {
        cell = startCellExpectingRegistration()
        val neighbors = Array(actorOf(new NeighborStub(testActor, 1)).start)
        cell ! ControllerToCellInitialize(true, neighbors)
        cell ! ControllerToCellStart
        expectMsg(('neighbor, 1, CellToCell(true, 0)))
        expectMsg(('board, CellToBoard(true, 0, 0, 0)))
        for (i <- 1 to 3) cell ! CellToCell(true, 0)
        for (i <- 1 to 5) cell ! CellToCell(false, 0)
        expectMsgAllOf(
          ('neighbor, 1, CellToCell(true, 1)),
          ('board, CellToBoard(true, 1, 0, 0)))
      }
    }
  }
}

class ControllerStub(testActor:ActorRef) extends Actor {
  def receive = {
    case msg => testActor ! ('controller, msg)
  }
}

class BoardStub(testActor:ActorRef) extends Actor {
  def receive = {
    case msg => testActor ! ('board, msg)
  }
}

class NeighborStub(testActor:ActorRef, id:Int) extends Actor {
  def receive = {
    case msg =>  testActor ! ('neighbor, id, msg)
  }
}

