package akka.tutorials.conway

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor._
import scala.Array._
import collection.mutable.ArrayBuffer

object Controller extends App {

  val xSize = 3
  val ySize = 3

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

  val boundaryCell = actorOf(new BoundaryCell).start()
  
  override def receive = {
    case ControllerInitialize => controllerInitialize()
  }
  
  def initialized: Receive = {
    case ControllerStart => controllerStart()
    case BoardToControllerAdvanceRound => advanceRound()
    case BoardToControllerDisplayRound(round: Int) => displayRound(round)
  }
  
  /**
   * Initalize the game. The game is initialized with the state from construction. 
   */
  private def controllerInitialize() {
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
    become(initialized)
  }
  
  /**
  * Initializes all cells in the board
  */
  private def initializeCells(){

     // Initialize all the cells
    for (x <- 0 to xSize-1){
      for (y <- 0 to ySize-1){
        var neighbors = new ArrayBuffer[ActorRef] ()

        for (xOffset <- -1 to 1){
            for (yOffset <- -1 to 1){
                neighbors += getNeighbor(x+xOffset, y+yOffset)
            }
        }
        cells(x)(y) ? ControllerToCellInitialize(initialStartState(x)(y), neighbors.toArray) 
      }
    }
  }  
  
  /**
   * Advances the round of the game. If the game is at its max round, then this repies with false. Otherwise true.
   */
  private def advanceRound() = {
    currentRound += 1
    if(currentRound < maxRounds) {
      
      self.reply(true)
    } else {
      self.reply(false)
      cells.flatten.foreach(_ ! ControllerToCellStop)
    }     
  }
  
  /**
   * Tell each cell to start
   */
  private def controllerStart() = cells.flatten.foreach(_ ! ControllerToCellStart)

    /**
     * This will return the neighbor located at (x)(y)
     * If that location is a boundary, then the boundary cell will be returned
     */
  private def getNeighbor(x:Int, y:Int):ActorRef = {
    if (x<0 || y <0 || x >= xSize || y >= ySize)
      boundaryCell
    else
      cells(x)(y)
  }
  
  private def displayRound(round: Int) = self.reply(round < maxRounds)
}