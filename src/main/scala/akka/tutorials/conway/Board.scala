package akka.tutorials.conway

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable.Map

/**
 * This represents the overall board.  Each cell tells the board their state on a round per round basis.
 * Once the board has received the state for all cells in a round it should send the complete board to the display actor.
 */
class Board(xSize:Int, ySize:Int, displayRef:ActorRef, controllerRef:ActorRef, maxRounds: Int) extends Actor{

  var boardList = IndexedSeq[Array[Array[Boolean]]]()
  
  val messageCountPerRound = Map[Int, Int]()
  
  var currentRound = 0
  
  /**
   * Create an empty board.  This should be called on new rounds.
   */
  def createBoard():Array[Array[Boolean]] =  { 
    Array.ofDim[Boolean](xSize, ySize)    
  }

  def complete: Receive = {
    case _ =>
  }
  
  def receive = {
    case CellToBoard(alive:Boolean, round:Int, x:Int, y:Int) => {
      if (boardList.size <= round) {
        boardList ++= Seq.fill(round - boardList.size + 1)(createBoard())
      }
      
      // Set the alive or dead
      boardList(round)(x)(y) = alive

      messageCountPerRound += round -> (messageCountPerRound.getOrElse(round, 0) + 1)
      
      if( (messageCountPerRound(round) == xSize * ySize) && ( round < maxRounds)) {
          currentRound += 1
          //the current round is greater than or equal to the max rounds so let's stop receiving msgs
          if(currentRound >= maxRounds) become(complete) 
          
          val boardState = boardList(round).clone()
          displayRef ! BoardState(round, boardState)
      }
    }
  }
}

