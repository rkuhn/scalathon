package akka.tutorials.conway

import akka.actor.ActorRef

case class ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef])
case object ControllerInitialize
case object ControllerToCellStart
case object ControllerToCellStop 
case object ControllerStart

case object BoardToControllerAdvanceRound
case class BoardToControllerDisplayRound(round: Int)

case class CellRegistration(x:Int, y:Int)
case class CellToCell(alive:Boolean, round:Int)
case class CellToBoard(alive:Boolean, round:Int, x:Int, y:Int)
case class RequestBoardState(round:Int)

case class BoardState(round:Int, boardState:Array[Array[Boolean]]) {
  override def equals(other: Any) : Boolean = {
    other match {
      case state: BoardState => {
        val boardStateFlat = boardState.flatten
        val otherBoardStateFlat = state.boardState.flatten
        var isEqual = true
        
        if( (boardState(0).size != state.boardState(0).size) ||  (boardState.length != state.boardState.length) )
          return false
          
        for(i <- 0 until boardState.length) {
          for(j <- 0 until boardState.length) {
            isEqual = boardState(i)(j) == state.boardState(i)(j)
          }
        }
        
        (round == state.round) 
      } 
      case _ => false
    }
    
  }
}
