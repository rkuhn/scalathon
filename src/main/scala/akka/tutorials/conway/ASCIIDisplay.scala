package akka.tutorials.conway

import akka.actor.{ActorRef, Actor}

/**
 * Very simple class to print out the board state.
 */
class ASCIIDisplay(board:ActorRef) extends Actor {

  override def receive = {
    case BoardState(round:Int, boardState:Array[Array[Boolean]]) =>
      if (boardState != null) {
        println("  -- Round " + round + " --")
        boardState.foreach( row => {
          row.foreach(
            if (_)
              print("+")
            else
              print(".")
          )
          println()
        }
        )
      }
  }

}