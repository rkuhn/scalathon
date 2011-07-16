package akka.tutorials.conway

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import javax.management.remote.rmi._RMIConnection_Stub

object Controller extends App {

  val xSize = 5
  val ySize = 5

  val initialStartState = Array.ofDim[Boolean](xSize, ySize)

  val display = actorOf(new ASCIIDisplay).start
  val controller = actorOf(new Controller(initialStartState, 10, display)).start

}

class Controller(initialStartState:Array[Array[Boolean]], maxRounds:Int, displayActor:ActorRef) extends Actor{

  var xSize:Int = 0
  var ySize:Int = 0

  // Create the Board actor
  var boardActor:ActorRef = _

  val cells = Array.ofDim[ActorRef](xSize, ySize)

  var cellRegistrationCount = 0

  // create the Cells
  override def preStart() {
    if (initialStartState.isEmpty)
      throw new IllegalArgumentException("The initial start state must not be an empty list")
    xSize = initialStartState.size
    ySize = initialStartState(0).size

     boardActor = actorOf(new Board(xSize, ySize, displayActor)).start()

    for (val x <- 0 to xSize){
      for (val y <- 0 to ySize){
        cells(x)(y) = actorOf(new Cell(x,y,this.self, boardActor)).start
      }
    }
  }


  override def receive = {
    case CellRegistration(x:Int, y:Int) => {
      cellRegistrationCount += 1

      if (cellRegistrationCount == xSize * ySize) {
        initializeCells()
      }
    }
  }

  // Send the initialization message to each cell
  def initializeCells(){
    // Initialize the edge cases first
    // TODO Actually initialize the edge cases

    // Initialize all the central cells
    for (val x <- 1 to xSize-1){
      for (val y <- 1 to ySize-1){
        // Super lazy way to do this initially. This should be cleaned up.
        val neighbors = Array[ActorRef](
          cells(x-1)(y),
          cells(x+1)(y),
          cells(x)(y-1),
          cells(x)(y+1),
          cells(x-1)(y-1),
          cells(x-1)(y+1),
          cells(x+1)(y-1),
          cells(x+1)(y+1)
        )
        cells(x)(y) ! ControllerToCellInitialize( initialStartState(x)(y), neighbors)
      }
    }

    //Start each cell
    cells.flatten.foreach(_ ! ControllerToCellStart)
  }


}