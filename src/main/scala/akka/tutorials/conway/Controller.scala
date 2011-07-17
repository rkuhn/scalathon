package akka.tutorials.conway

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import javax.management.remote.rmi._RMIConnection_Stub
import scala.Array._
import collection.mutable.ArrayBuffer

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

  var cells:Array[Array[ActorRef]] = _

  var cellRegistrationCount = 0

  val boundaryCell = actorOf(new BoundaryCell).start()

  // create the Cells
  override def preStart() {
    if (initialStartState.isEmpty)
      throw new IllegalArgumentException("The initial start state must not be an empty list")
    xSize = initialStartState.size
    ySize = initialStartState(0).size

     boardActor = actorOf(new Board(xSize, ySize, displayActor)).start()

    cells = Array.ofDim[ActorRef](xSize, ySize)
    
    for (x <- 0 to xSize){
      for (y <- 0 to ySize){
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

     // Initialize all the cells
    for (x <- 0 to xSize-1){
      for (y <- 0 to ySize-1){
        var neighbors = new ArrayBuffer[ActorRef] ()

        for (xOffset <- -1 to 1){
            for (yOffset <- -1 to 1){
              if (x!=0 || y !=0)
                neighbors += getNeighbor(x+xOffset, y+yOffset)
            }
        }
        cells(x)(y) ! ControllerToCellInitialize( initialStartState(x)(y), neighbors.toArray)

      }
    }

    //Start each cell
    cells.flatten.foreach(_ ! ControllerToCellStart)
  }

    /**
     * This will return the neighbor located at (x)(y)
     * If that location is a boundary, then the boundary cell will be returned
     */
  def getNeighbor(x:Int, y:Int):ActorRef = {
    if (x<0 || y <0 || x >= xSize || y >= ySize)
      boundaryCell
    else
      cells(x)(y)
  }

}