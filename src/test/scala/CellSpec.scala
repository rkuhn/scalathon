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
       within(100 millis) {
         startCellExpectingRegistration() 
      }
    }
    "error upon attempt to start before initialized " in {
      within(100 millis) {
        val future = startCellExpectingRegistration() ? ControllerToCellStart
        evaluating {future.await.get} should produce [UnhandledMessageException]
      }
    }
    "error upon attempt to respond to other cells before initialized " in {
      within(100 millis) {
        val future = startCellExpectingRegistration() ? CellToCell(true, 1) 
        evaluating {future.await.get} should produce [UnhandledMessageException]
      }
    }
    "notify the board its alive when it receives 3 alive and 5 dead neighbor messages" in {
      within (1000 millis) {
        cell = startCellExpectingRegistration()
        val dummyNeighbor = actorOf(new DummyReceiver())
        dummyNeighbor.start
        cell ! ControllerToCellInitialize(true, Array(dummyNeighbor))
        cell ! ControllerToCellStart
        for (i <- 1 to 3) cell ! CellToCell(true, 0)
        for (i <- 1 to 5) cell ! CellToCell(false, 0)
        expectMsg(('board, CellToBoard(true, 0, 0, 0)))
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

class DummyReceiver() extends Actor {
  def receive = {
    case _ => 
  }
}

