package akka.tutorials.conway

import akka.actor.Actor

/** This class represents the boundary of the life board. It always replies to a
  * CellToCell message with a dead CellToCell message for the same round.
  */ 
class BoundaryCell extends Actor {
  override def receive = {
    case CellToCell(alive:Boolean, round:Int) => reply CellToCell(false, round)
  }
}

