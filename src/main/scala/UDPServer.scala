import java.nio.charset.StandardCharsets

import zio._
import zio.clock._
import zio.console._
import zio.random._
import zio.nio.channels.{AsynchronousServerSocketChannel, AsynchronousSocketChannel, DatagramChannel}
import zio.nio._
import zio.duration._
import zio.nio.core.{Buffer, ByteBuffer, SocketAddress}

object UDPServer {


  def start(port: Int) = {
    for {
      address <- SocketAddress.inetSocketAddress(port)
      _       <- putStrLn("Listening on port: " + port)
      worker  <- DatagramChannel.bind(Some(address)).use(accept(_)).forever
    } yield ()
  }

  def accept(server:DatagramChannel) = {
    for {
      sink    <- Buffer.byte(512)
      retAddress <- server.receive(sink)
      msg <- byteBuffer2String(sink)
      _ <- putStrLn("Received: " + msg) //StandardCharsets.UTF_8.decode(jbuf).toString())
      addr <- ZIO.fromOption(retAddress)
      _ <- sink.flip
      _ <- server.send(sink, addr)
    } yield ()
  }



  def byteBuffer2String(bb: zio.nio.core.ByteBuffer) = {

    for {
      spos <- bb.position
      bytes <- ZIO.collectAll((0 until spos).map(a => bb.get(a))).map(a => a)
      txt <- ZIO.succeed(new String(bytes.toArray))
    } yield txt
  }

  //  def echoClient(address: SocketAddress): IO[Exception, Boolean] =
  //    for {
  //      src    <- Buffer.byte(3)
  //      result <- DatagramChannel.connect(address).use { client =>
  //        for {
  //          sent     <- src.array
  //          _         = sent.update(0, 1)
  //          _        <- client.send(src, address)
  //          _        <- src.flip
  //          _        <- client.read(src)
  //          received <- src.array
  //        } yield sent.sameElements(received)
  //      }
  //    } yield result

  //  private def accept(reservation: Reservation[Any, Exception, AsynchronousSocketChannel]) =
  //    reservation.acquire.flatMap { socket =>
  //      socket.read(24, 30.seconds)
  //        .flatMap(chunk => putStrLn("Read chunk: " + chunk.mkString))
  //        .whenM(socket.isOpen)
  //        .forever
  //        .ensuring(reservation.release(Exit.Success(0)) *> putStrLn("Connection closed."))
  //        .catchAll(e => putStrLn("Connection closed due to: " + e.getMessage))
  //        .fork
  //    }

}