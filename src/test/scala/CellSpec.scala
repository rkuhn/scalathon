package akka.tutorials.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.testkit.TestKit

class CellSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
  val controller = actorOf(new ControllerStub).start()
  val board = actorOf(new BoardStub).start()
  val cell = actorOf(new Cell(0,0,controller,board)).start()

  override protected def afterAll() {
    cell.stop()
  }
}

class ControllerStub extends Actor {
  def receive = {
    case _ => None
  }
}

class BoardStub extends Actor {
  def receive = {
    case _ => None
  }
}
