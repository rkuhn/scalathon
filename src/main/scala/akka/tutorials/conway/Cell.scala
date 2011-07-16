package akka.tutorials.conway

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor._

class Cell(val x:Int, val y:Int, controller:ActorRef, val board:ActorRef) extends Actor {
  var alive:Boolean = _
  var neighbors:Array[ActorRef] = _
  var currentRound:Int = 0
  var currentRoundState:NeighborsState = new NeighborsState()
  var nextRoundState:NeighborsState = new NeighborsState() 

  override def preStart() {
    controller ! CellRegistration(x, y)
  }

  override def receive = {
    case ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef]) => 
      this.alive = alive
      this.neighbors = neighbors
    case ControllerToCellStart => 
      neighbors.foreach(n => n ! CellToCell(alive, currentRound))
      board ! CellToBoard(alive, currentRound, x, y)
    case CellToCell(alive:Boolean, round:Int) => 
      /* TODO: if round is complete, update the board, and reset the round states. */
      if  (currentRound == round) 
        currentRoundState.update(alive)
      else if (currentRound + 1 == round)
        nextRoundState.update(alive)
      else
        println("How did we get here?")
  }
}

class NeighborsState {
  private var dead_ = 0
  private var alive_ = 0

  def update(alive:Boolean) {
    if (alive) incrementAlive else incrementDead
  }

  def alive = alive_
  def isComplete:Boolean = dead_ + alive_ == 8

  /* Get rid of these, or make private? */
  def incrementDead() { dead_ = dead_ + 1 }

  def incrementAlive() { alive_ = alive_ + 1 }
}

