package akka.tutorials.conway

import akka.actor.ActorRef

case class ControllerToCellInitialize(alive:Boolean, neighbors:Array[ActorRef])
case class ControllerToCellStart()
case class ControllerToCellStop()

case class CellRegistration(x:Int, y:Int)
case class CellToCell(alive:Boolean, round:Int)
case class CellToBoard(alive:Boolean, round:Int, x:Int, y:Int)

case class RequestBoardState(round:Int)
case class BoardState(round:Int, boardState:Array[Array[Boolean]])
