package com.lightbend.akka.sample.IoT

import akka.actor.{ActorRef, Props, Actor}

import scala.concurrent.duration.FiniteDuration

object DeviceGroupQuery {
  case object CollectionTimeout

  def props(
           actorToDeviceId: Map[ActorRef, String],
           requestId: Long,
           requester: ActorRef,
           timeout: FiniteDuration
           ): Props = {
    Props()
  }
}

class DeviceGroupQuery(
                        actorToDeviceId: Map[ActorRef, String],
                        requestId: Long,
                        requester: ActorRef,
                        timeout: FiniteDuration
                      ) extends Actor {
  import DeviceGroupQuery._
  import context.dispatcher

  val queryTimeoutTime = context.system.scheduler.scheduleOnce(timeout, self, CollectionTimeout)

  override def preStart(): Unit = {
    actorToDeviceId.keysIterator.foreach {
      deviceActor =>
        context.watch(deviceActor)
        deviceActor ! Device.ReadTemperature
    }
  }

  override def postStop(): Unit = {
    queryTimeoutTime.cancel()
  }

  override def receive: Receive = ???

  def waitingForReplies (
    repliesSoFar: Map[String, DeviceGroup.TemperatureReading],
    stillWaiting: Set[ActorRef]
  ): Receive = {
    case Device.RespondTemperature(0, valueOption) =>
      val deviceActor = sender()
      val reading = valueOption match {
        case Some(value) => DeviceGroup.Temperature(reading)
        case None => DeviceGroup.TemperatureNotAvailable
      }
      // receivedResponse()
  }
}