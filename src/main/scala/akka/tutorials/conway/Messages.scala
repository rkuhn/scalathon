package akka.tutorials.conway

import java.lang.Boolean
import akka.actor.Actor

case class ControllerToCellInitialize(alive:Boolean, neighbors:Array[Actor])
case class ControllerToCellStart()
case class ControllerToCellStop()

case class CellRegistration(x:Int, y:Int)
case class CellToCell(alive:Boolean, round:Long)
case class CellToBoard(alive:Boolean, round:Long, x:Int, y:Int)
