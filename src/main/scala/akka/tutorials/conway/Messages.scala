package akka.tutorials.conway

import akka.actor.ActorRef

case class ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef])
case class ControllerInitialize()
case class ControllerToCellStart()
case class ControllerToCellStop()

case class CellRegistration(x:Int, y:Int)
case class CellToCell(alive:Boolean, round:Int)
case class CellToBoard(alive:Boolean, round:Int, x:Int, y:Int)

case class RequestBoardState(round:Int)

case class BoardState(round:Int, boardState:Array[Array[Boolean]]) {
  override def equals(other: Any) = {
    other match {
      case state: BoardState => {
        
        val boardStateFlat = boardState.flatten
        val otherBoardStateFlat = state.boardState.flatten
        
        var isEqual = true
        if(boardStateFlat.length != otherBoardStateFlat.length)
          isEqual = false
        for(i <- 0 until boardStateFlat.length) {
          isEqual = boardStateFlat(i) == otherBoardStateFlat(i)
        }
        
        (round == state.round) && isEqual
      } 
      case _ => false
    }
    
  }
}
