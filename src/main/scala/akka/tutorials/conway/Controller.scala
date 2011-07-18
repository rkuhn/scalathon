package akka.tutorials.conway

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import javax.management.remote.rmi._RMIConnection_Stub
import scala.Array._
import collection.mutable.ArrayBuffer

object Controller extends App {

  val xSize = 2
  val ySize = 1

  val initialStartState = Array.ofDim[Boolean](xSize, ySize)

  val display = actorOf(new ASCIIDisplay).start()
  val controller = actorOf(new Controller(initialStartState, 5, display)).start()

  controller ! ControllerInitialize
  controller ! ControllerStart

}

class Controller(initialStartState:Array[Array[Boolean]], maxRounds:Int, displayActor:ActorRef) extends Actor{

  var xSize:Int = 0
  var ySize:Int = 0
  var currentRound = 0
  
  // Create the Board actor
  var boardActor:ActorRef = _

  var cells:Array[Array[ActorRef]] = _

  var cellRegistrationCount = 0

  val boundaryCell = actorOf(new BoundaryCell).start()
  
  override def receive = {
    case ControllerInitialize => controllerInitialize() 
    case ControllerStart => controllerStart()
    case BoardToControllerAdvanceRound  => advanceRound()
  }
  
  // create the Cells
  def controllerInitialize() {
    if (initialStartState.isEmpty)
      throw new IllegalArgumentException("The initial start state must not be an empty list")
      
    xSize = initialStartState.size
    ySize = initialStartState(0).size

    boardActor = actorOf(new Board(xSize, ySize, displayActor, this.self)).start()

    cells = Array.ofDim[ActorRef](xSize, ySize)
    
    for (x <- 0 until xSize){
      for (y <- 0 until ySize){ 
        cells(x)(y) = actorOf(new Cell(x,y,this.self, boardActor)).start()
      }
    }
    initializeCells()
  }
  
  // Send the initialization message to each cell
  def initializeCells(){

     // Initialize all the cells
    for (x <- 0 to xSize-1){
      for (y <- 0 to ySize-1){
        var neighbors = new ArrayBuffer[ActorRef] ()

        for (xOffset <- -1 to 1){
            for (yOffset <- -1 to 1){
                neighbors += getNeighbor(x+xOffset, y+yOffset)
            }
        }
        //on initalization, let's block until we know that all cells are successfully initialized
        cells(x)(y) ? ControllerToCellInitialize(initialStartState(x)(y), neighbors.toArray) 
      }
    }
  }  
  
  def advanceRound() = {
    currentRound += 1
    if(currentRound == maxRounds) cells.flatten.foreach(_ ! ControllerToCellStop)      
  }
  
  def controllerStart() {
    if(initialStartState.isEmpty) {
      throw new IllegalStateException("The game has not been initialized")
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