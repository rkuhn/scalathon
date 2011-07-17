package akka.tutorials.conway

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable.Map

/**
 * This represents the overall board.  Each cell tells the board their state on a round per round basis.
 * Once the board has received the state for all cells in a round it should send the complete board to the display actor.
 */
class Board(xSize:Int, ySize:Int, displayRef:ActorRef) extends Actor{

  var boardList = List[Array[Array[Boolean]]]()
  
  val messageCountPerRound = Map[Int, Int]()

  /**
   * Create an empty board.  This should be called on new rounds.
   */
  def createBoard():Array[Array[Boolean]] =  { 
    println("creating board")
    Array.ofDim[Boolean](xSize, ySize)    
  }

  def receive = {
    // Handle the CellToBoard message
    case CellToBoard(alive:Boolean, round:Int, x:Int, y:Int) => {
      println("received CellToBoard")
      if (boardList.size <= round)
        boardList = boardList :+ createBoard()
      // Set the alive or dead
      boardList(round)(x)(y) = alive

      messageCountPerRound += round -> (messageCountPerRound.getOrElse(round, 0) + 1)
      
      println("messageCountPerRound: " + messageCountPerRound(round))

      // Check if we have heard from all the cells.  If we have, send the state to the display actor.
      if(messageCountPerRound(round) == xSize * ySize) {
          println("In if.. going to send message")
          val boardState = boardList(round).clone()
          displayRef ! BoardState(round, boardState)

      }
    }
  }
}

