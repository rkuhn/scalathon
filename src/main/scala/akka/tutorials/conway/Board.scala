package akka.tutorials.conway

import akka.actor.Actor

class Board(xSize:Int, ySize:Int, maxRoundsToKeep:Int) extends Actor{

  var boardList = List[Array[Array[Boolean]]]()

  def createBoard():Array[Array[Boolean]] = Array.ofDim[Boolean](xSize, ySize)

  def receive = {
    // Handle the CellToBoard message
    case CellToBoard(alive:Boolean, round:Int, x:Int, y:Int) => {
      if (boardList.size <= round)
        boardList :+ createBoard()
      // Set the alive or dead
      board(round)(x)(y) = alive
    }

    case RequestBoardState(round:Int) => {
      if (round > boardList.size )
        self reply createBoard()
      else {
        val boardState =  boardList(round).clone()
        self reply BoardState(round, boardState)
      }
    }
  }

}