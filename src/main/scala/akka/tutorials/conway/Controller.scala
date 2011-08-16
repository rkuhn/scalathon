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

  var currentRound = 0
  
  // Create the Board actor
  val xSize = initialStartState.size
  val ySize = initialStartState(0).size
  val boardActor = actorOf(new Board(xSize, ySize, displayActor, this.self, maxRounds)).start()
  val cells = Array.tabulate(xSize, ySize)((x, y) => actorOf(new Cell(x, y, self, boardActor), "cell"+ x + y).start())

  val boundaryCell = actorOf(new BoundaryCell).start()
  
  override def receive = {
    case ControllerInitialize => controllerInitialize()
  }
  
  def initialized: Receive = {
    case ControllerStart => controllerStart()
    case BoardToControllerAdvanceRound => advanceRound()
  }
  
  /**
   * Initalize the game. The game is initialized with the state from construction. 
   */
  private def controllerInitialize() = {
    initializeCells()
    become(initialized)
  }
  
  /**
  * Initializes all cells in the board
  */
  private def initializeCells() = {
     // Initialize all the cells
    for (x <- 0 to xSize-1){
      for (y <- 0 to ySize-1){
        val neighbors = for {
          xOffset <- (-1) to 1
          yOffset <- (-1) to 1
          if( xOffset != 0 || yOffset != 0)
          } yield {
              getNeighbor(x+xOffset, y+yOffset)
          }
          cells(x)(y) ! ControllerToCellInitialize(initialStartState(x)(y), neighbors.toArray) 
        }        
      }
      self.tryReply(true)
    }  
  
  /**
   * Advances the round of the game. If the game is at its max round, tell Cells to stop
   */
  private def advanceRound() = {
    currentRound += 1
    if(currentRound >= maxRounds) cells.flatten.foreach(_ ! ControllerToCellStop)
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
}