package com.lightbend.akka.sample.IoT

import akka.actor.{Actor, ActorLogging, Props}

object IotSupervisor {
  def props(): Props = Props(new IotSupervisor)
}

class IotSupervisor extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("IoT Application Started")

  override def postStop(): Unit = log.info("Iot Application Stopped")

  override def receive: Receive = {
    case "start" =>
      context.actorOf(DeviceManager.props(), "device-manager")
  }

}
