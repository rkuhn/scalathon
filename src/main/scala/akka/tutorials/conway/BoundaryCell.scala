package akka.tutorials.conway

import akka.actor.Actor
import akka.actor.Actor._
import akka.routing._

class BoundaryCell extends Actor with DefaultActorPool
                                     with DefaultActorPoolSupervisionConfig
                                     with BoundedCapacityStrategy
                                     with SmallestMailboxSelector
                                     with MailboxPressureCapacitor
                                     with Filter
                                      with RunningMeanBackoff
                                      with BasicRampup
{
  def receive = _route
  def lowerBound = 1
  def upperBound = 5
  def pressureThreshold = 1
  def partialFill = true
  def selectionCount = 1
  def rampupRate = 0.1
  def backoffRate = 0.50
  def backoffThreshold = 0.50
  def instance = actorOf(new Actor {
    def receive = { case CellToCell(alive:Boolean, round:Int) => self.reply(CellToCell(false, round)) }
  })
  
}
                          