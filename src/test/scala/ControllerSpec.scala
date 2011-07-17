package akka.tutorial.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import akka.testkit.TestKit
import akka.testkit.TestKit._
import akka.util.duration._
import akka.tutorials.conway._

class ControllerSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
    
    val xSize = 5
    val ySize = 5
    
    val initialStartState = Array.ofDim[Boolean](xSize, ySize)
    val maxRounds =  5
    
    "A ControllerActor" should {
      " should initalize all of the neighbors when receiving a cell's registration" in {
        val controllerActor = actorOf(new Controller(initialStartState, maxRounds, testActor)).start()
        within (1000 millis) {
          controllerActor ! ControllerInitialize
        }
      }
    }
    
}

