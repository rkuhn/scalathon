package akka.tutorials.conway

import akka.actor.ActorRef
import collection.mutable.StringBuilder

case class ControllerToCellInitialize(alive: Boolean, neighbors: Array[ActorRef])

case object ControllerInitialize

case object ControllerToCellStart

case object ControllerToCellStop

case object ControllerStart

case object BoardToControllerAdvanceRound

case class CellRegistration(x: Int, y: Int)

case class CellToCell(alive: Boolean, round: Int)

case class CellToBoard(alive: Boolean, round: Int, x: Int, y: Int)

case class RequestBoardState(round: Int)

case class BoardState(round: Int, boardState: Array[Array[Boolean]]) {

  override def toString() = {
    val builder = new StringBuilder()
    builder.append("\n[Round: " + round)

    builder.append("\n")
    for (i <- 0 until boardState.length) {
      for (j <- 0 until boardState.length) {
        builder.append("Board[%d,%d]=%b".format(i, j, boardState(i), (j)))
        builder.append("\n")
      }
    }
    builder.toString()
  }

  override def equals(other: Any): Boolean = {
    other match {
      case state: BoardState => {
        var isEqual = true

        if ((boardState(0).size != state.boardState(0).size) || (boardState.length != state.boardState.length))
          return false

          for (i <- 0 until boardState.length) {
            for (j <- 0 until boardState.length) {
              isEqual = boardState(i)(j) == state.boardState(i)(j)
              if(!isEqual)
                return false
            }
          }

        true
      }
      case _ => false
    }

  }
}
