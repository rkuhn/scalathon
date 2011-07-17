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

  controller ! ControllerInitialize
  controller ! ControllerStart

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
   def controllerInitialize() {
    if (initialStartState.isEmpty)
      throw new IllegalArgumentException("The initial start state must not be an empty list")
      
    xSize = initialStartState.size
    ySize = initialStartState(0).size

    boardActor = actorOf(new Board(xSize, ySize, displayActor)).start()

    cells = Array.ofDim[ActorRef](xSize, ySize)
    
    for (x <- 0 until xSize){
      for (y <- 0 until ySize){
        cells(x)(y) = actorOf(new Cell(x,y,this.self, boardActor)).start
      }
    }
    initializeCells()
  }
  
  def controllerStart() {
    if(initialStartState.isEmpty) {
      throw new IllegalStateException("The game has not been initialized")
    }
    
    //Start each cell
    cells.flatten.foreach(_ ! ControllerToCellStart)
  }


  override def receive = {
    case ControllerInitialize => controllerInitialize() // this will not also init cells
   
    case CellRegistration(x:Int, y:Int) => { // cell regristration is deprecated no need to do it anymore
      cellRegistrationCount += 1
      if (cellRegistrationCount == xSize * ySize) {
        initializeCells()
      }
    }
    case ControllerStart => controllerStart()
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
        //on initalization, let's block until we know that all cells are successfully initialized
        cells(x)(y) ? ControllerToCellInitialize(initialStartState(x)(y), neighbors.toArray) 
      }
    }
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