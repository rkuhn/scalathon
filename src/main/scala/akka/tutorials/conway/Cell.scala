package akka.tutorials.conway

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor._

class Cell(val x:Int, val y:Int, controller:ActorRef, val board:ActorRef) extends Actor {
  var alive:Boolean = _
  var neighbors:Array[ActorRef] = _
  var round:Int = 0

  override def preStart() {
    controller ! CellRegistration(x, y)
  }

  override def receive = {
    case ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef]) => 
      this.alive = alive
      this.neighbors = neighbors
    case ControllerToCellStart => 
      neighbors.foreach(n => n ! CellToCell(alive, round))
      board ! CellToBoard(alive, round, x, y)
      /* TODO: Not handling CellToCell or ControllerToCellStart */
  }
}

