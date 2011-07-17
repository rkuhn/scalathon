package akka.tutorials.conway

import akka.actor.Actor

/**
 * Very simple class to print out the board state.
 */
class ASCIIDisplay extends Actor {

  override def receive = {
    case BoardState(round:Int, boardState:Array[Array[Boolean]]) =>
      if (boardState != null) {
        // Convert all the true values to '+' and false values to '.'
        val board = boardState.par.map(_.map(
          if (_)
            "+"
          else
            "."
        ))
        // Make a pretty matrix display of the '+' and '.' values
        .map(_.mkString).mkString("\n")

        println ("-- Round " + round + " --" +"\n" +
                board)
      }
  }

}