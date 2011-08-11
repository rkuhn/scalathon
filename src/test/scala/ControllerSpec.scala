package akka.tutorials.conway

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{WordSpec, BeforeAndAfterEach}
import akka.actor.{ActorRef, Actor, ActorRegistry}
import akka.actor.Actor._
import akka.actor.Actor.registry._
import akka.testkit.TestKit
import akka.testkit.TestKit._
import akka.util.duration._
import akka.tutorials.conway._
import akka.actor._
import akka.event._

class ControllerSpec extends WordSpec with BeforeAndAfterEach with ShouldMatchers with TestKit  {
    
  val xSize = 2
  val ySize = 2
  
  val initialStartState = Array.ofDim[Boolean](xSize, ySize)
  val maxRounds =  5
  
  var actors: Array[ActorRef] = Array[ActorRef]()
 
  override def afterEach {  Actor.registry.local.actors foreach (_.stop()) }
  
  "A ControllerActor" should {
    
    "create all cells when initializing" in {
      val controllerActor = actorOf(new Controller(initialStartState, maxRounds, testActor)).start()
      within (1000 millis) {
        (controllerActor ? ControllerInitialize).await.get
        val cells = Actor.registry.local.filter{_.address.startsWith("cell")}
        cells.size should equal (4)
      }
    }
    "throw an exception if BoardToControllerAdvanceRound is called before ControllerInitialize" in {
      val controllerActor = actorOf(new Controller(initialStartState, maxRounds, testActor)).start()
      within (1000 millis) {
        evaluating {
          (controllerActor ? BoardToControllerAdvanceRound).await.get
        } should produce [UnhandledMessageException] 
      }
    }
    "initialize all Cells" in {
      val controllerActor = actorOf(new Controller(initialStartState, maxRounds, testActor)).start()
      within (10000 millis) {
        val result = (controllerActor ? ControllerInitialize).await.get
        result should equal (true)
        val cells = Actor.registry.local.filter{_.address.startsWith("cell")}
        actors.foreach((actor: ActorRef) => actor ! ControllerToCellStart)
      }
    }
  }       
}
