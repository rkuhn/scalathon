package akka.tutorials.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.testkit.TestKit
import akka.util.duration._

class CellSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
  val controller = actorOf(new ControllerStub(testActor)).start()
  val board = actorOf(new BoardStub).start()
  val cell = actorOf(new Cell(0,0,controller,board))

  override protected def afterAll() {
    cell.stop()
  }

  "A Cell" should {
    "Send a registration to the controller" in {
      within(1000 millis) {
        cell.start()
        expectMsg(CellRegistration(0,0))
      }
    }
  }
}

class ControllerStub(testActor:ActorRef) extends Actor {
  def receive = {
    case msg => testActor ! msg
  }
}

class BoardStub extends Actor {
  def receive = {
    case _ => None
  }
}
