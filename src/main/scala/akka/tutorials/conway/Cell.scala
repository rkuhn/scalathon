package akka.tutorials.conway

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor._

/** Represents a cell in the life grid. Once initialized, this responds to messages from
  * its neighbors, and when it gets messages from all of its neighbors for the current 
  * round, it calculates its own state for the next round, notifies the board and all 
  * neighbors what it will be next round.
  */
class Cell(val x:Int, val y:Int, controller:ActorRef, val board:ActorRef) extends Actor {
  var alive:Boolean = _
  var neighbors:Array[ActorRef] = _
  var currentRound:Int = 0
  var currentRoundState:NeighborsState = new NeighborsState()
  var nextRoundState:NeighborsState = new NeighborsState() 

  /*
  override def preStart() {
    controller ! CellRegistration(x, y)
  }
  */
  override def receive = {
    case ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef]) => 
      this.alive = alive
      this.neighbors = neighbors
      become(initialized)
  }

  def initialized:Receive = {
    case ControllerToCellStart =>  sendState
    case CellToCell(alive:Boolean, round:Int) => 
      if  (currentRound == round) { 
        currentRoundState.update(alive)
        if (currentRoundState.isComplete) completeRound
      }
      else if (currentRound + 1 == round)
        nextRoundState.update(alive)
      else
        println("How did we get here?")
  }

  private def sendState() {
    neighbors.foreach(n => n ! CellToCell(alive, currentRound))
    board ! CellToBoard(alive, currentRound, x, y)
  }
    

  private def completeRound() {
    alive = (currentRoundState.alive == 3 || (currentRoundState.alive == 2 && alive))
    currentRound = currentRound + 1
    sendState
    currentRoundState = nextRoundState
    nextRoundState = new NeighborsState
  }

}

/** This class is used by Cell actor to maintain the state
  * of all of its neighbors for a specified round.
  */
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

