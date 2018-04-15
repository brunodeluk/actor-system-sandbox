package com.lightbend.akka.sample.IoT

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.lightbend.akka.sample.IoT.DeviceManager.RequestTrackDevice

object DeviceManager {
  def props(): Props = Props(new DeviceManager)
  final case class RequestTrackDevice(groupId: String, deviceId: String)
  case object DeviceRegistered
}

class DeviceManager extends Actor with ActorLogging {
  var groupIdToActor = Map.empty[String, ActorRef]
  var actorToGroupId = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("Device Manager started")

  override def postStop(): Unit = log.info("Device Manager Stopped")

  override def receive: Receive = {
    case trackMsg @ RequestTrackDevice(groupId, _) =>
      groupIdToActor.get(groupId) match {
        case Some(ref) =>
          ref forward trackMsg
        case None =>
          log.info("Creating device group actor for {}", groupId)
          val groupDevice: ActorRef = context.actorOf(DeviceGroup.props(groupId), s"DeviceGroup-${groupId}")
          groupIdToActor += groupId -> groupDevice
          actorToGroupId += groupDevice -> groupId
          groupDevice forward trackMsg
      }

    case Terminated(groupActor) =>
      val groupId = actorToGroupId(groupActor)
      log.info("Device group actor for {} has been terminated", groupId)
      actorToGroupId -= groupActor
      groupIdToActor -= groupId
  }
}