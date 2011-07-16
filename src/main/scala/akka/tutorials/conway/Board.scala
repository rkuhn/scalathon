package akka.tutorials.conway

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable.Map

class Board(xSize:Int, ySize:Int, displayRef:ActorRef) extends Actor{

  var boardList = List[Array[Array[Boolean]]]()
  
  val messageCountPerRound = Map[Int, Int]()  
  
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
      
      if(messageCountPerRound(round) == xSize * ySize) {
          println("In if.. going to send message")
          val boardState = boardList(round).clone()
          displayRef ! BoardState(round, boardState)

      }
    }
  }
}

