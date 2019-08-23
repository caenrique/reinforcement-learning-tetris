package com.uhu.cesar.tetris

import java.io.{Closeable, IOException}
import java.net.{DatagramPacket, DatagramSocket, InetAddress, SocketException}

import com.uhu.cesar.tetris.Message.{BadMessage, EndMessage, MessageParser, MovMessage}


trait Player {
  mp: MessageParser =>

  var numberOfGames = 0

  val LOGIN: String
  val NAME: String

  def init(): Unit

  def think(perception: Message): Respuesta

  def restart(): Boolean

  val SEND_PORT = 4567
  val RECEIVE_PORT = 5678

  def gameLoop(): Unit = {

    def closing[A, B <: Closeable](c: B)(f: B => A): A = try f(c) finally c.close()

    closing(new DatagramSocket(RECEIVE_PORT)) { socket =>
      val bufer = Array.ofDim[Byte](1000)

      while (true) {
        // PERCIBIR
        val peticion = new DatagramPacket(bufer, bufer.length)
        socket.receive(peticion)
        val percepcion = new String(peticion.getData, 0, peticion.getLength)

        // PENSAR
        val message = parseMessage(percepcion)

        message match {
          case m: MovMessage =>
            send(think(m).toString)
            send("CAER")
          case m: EndMessage => if (restart()) start() else println("End of the Game")
          case m: BadMessage => throw new Exception(s"Bad Message: ${m.msg}")
        }
      }
    }
  }

  def start(): Unit = {
    numberOfGames = numberOfGames + 1
    val startMessage = s"start;$LOGIN;$NAME;"
    send(startMessage)
  }

  def send(mm: String): Unit = {
    try {
      val socketUDP = new DatagramSocket()
      val respuesta = new DatagramPacket(mm.getBytes(), mm.length(), InetAddress.getByName("localhost"), SEND_PORT)
      socketUDP.send(respuesta)
      socketUDP.close()
    } catch {
      case e: SocketException => println("Socket: " + e.getMessage);
      case e: IOException => println("IO: " + e.getMessage);
    }
  }

}
