package com.lightbend.akka.sample

import akka.actor.{Actor, ActorSystem, Props}
import scala.io.StdIn

class StartStopActor1 extends Actor {

  override def preStart(): Unit = {
    println("first started")
    context.actorOf(Props[StartStopActor2], "second-actor")
  }

  override def postStop(): Unit = println("first stopped")

  override def receive: Receive = {
    case "stop" => context.stop(self)
  }
}

class StartStopActor2 extends Actor {

  override def preStart(): Unit = println("second started")

  override def postStop(): Unit = println("second stopped")

  override def receive: Receive = {
    Actor.emptyBehavior
  }
}

object StartStopActorsMain extends App {
  val system = ActorSystem("StartStopSystem")

  val first = system.actorOf(Props[StartStopActor1], "first")

  try StdIn.readLine()
  finally first ! "stop"

}