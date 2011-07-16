package akka.tutorials.conway

import akka.actor.ActorRef

case class ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef])
case class ControllerToCellStart()
case class ControllerToCellStop()

case class CellRegistration(x:Int, y:Int)
case class CellToCell(alive:Boolean, round:Long)
case class CellToBoard(alive:Boolean, round:Long, x:Int, y:Int)
