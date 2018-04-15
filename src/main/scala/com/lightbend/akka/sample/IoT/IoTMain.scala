package com.lightbend.akka.sample.IoT

import akka.actor.{ActorSystem}
import com.lightbend.akka.sample.IoT.DeviceManager.RequestTrackDevice

import scala.io.StdIn

object IoTMain extends App {

  val system = ActorSystem("iot-system")

  try {
    val deviceManager = system.actorOf(DeviceManager.props(), "device-manager")
    deviceManager ! RequestTrackDevice("group1", "device1")
    StdIn.readLine()
  }
  finally {
    system.terminate()
  }

}
