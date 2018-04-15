package com.lightbend.akka.sample

import akka.actor.{ Actor, Props, ActorSystem, ActorRef }
import scala.io.StdIn

class PrintMyActorRef extends Actor {


  override def receive: Receive = {
    case "printit" =>
      val secondRef: ActorRef = context.actorOf(Props.empty, "second-actor")
      println(s"Second: $secondRef")
  }
}

class SupervisingActor extends Actor {

  val child = context.actorOf(Props[SupervisedActor], "supervised-actor")

  override def receive: Receive = {
    case "failChild" => child ! "fail"
  }
}

class SupervisedActor extends Actor {


  override def preStart(): Unit = println("supervised actor started")

  override def receive: Receive = {
    case "fail" =>
      println("supervised actor fails now")
      throw new Exception("I failed!")
  }
}

object ActorHierarchyExperiments extends App {
  val system = ActorSystem("testSystem")

//  val firstRef: ActorRef = system.actorOf(Props[PrintMyActorRef], "first-actor")
//  println(s"First: $firstRef")
//  firstRef ! "printit"
//
//  println(">>> Press ENTER to exit <<<")
//  try StdIn.readLine()
//  finally system.terminate()

  val supervisingActor: ActorRef = system.actorOf(Props[SupervisingActor], "supervising-actor")
  supervisingActor ! "failChild"
}