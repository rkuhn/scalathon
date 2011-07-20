package akka.tutorials.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterAll}
import akka.actor.{ActorRef, Actor, ActorRegistry}
import akka.actor.Actor._
import akka.actor.Actor.registry._
import akka.testkit.TestKit
import akka.testkit.TestKit._
import akka.util.duration._
import akka.tutorials.conway._

class ControllerSpec extends WordSpec with BeforeAndAfterAll with ShouldMatchers with TestKit {
    
    val xSize = 5
    val ySize = 5
    
    val initialStartState = Array.ofDim[Boolean](xSize, ySize)
    val maxRounds =  5
    val controllerActor = actorOf(new Controller(initialStartState, maxRounds, testActor)).start()
    
    "A ControllerActor" should {
      " should initalize all of the neighbors when receiving a cell's registration" in {
        
        within (1000 millis) {
          controllerActor ! ControllerInitialize
          //akka.actor.Actor.registry.filter{Cell => true}.size should equal (25)
          //boardCount = akka.actor.Actor.registry.filter(classOf[Board]).size should equal (1)      
        }
      }
      " should respond with true when a value of less than the max number of rounds is sent with the display round message" in {
        val future = controllerActor ? BoardToControllerDisplayRound(3)
        val displayRound: Any = future.get
        displayRound should equal(true)
      }  
    }
        
}

