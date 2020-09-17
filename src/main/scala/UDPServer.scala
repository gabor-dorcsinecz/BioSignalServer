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


  def start(port: Int): RIO[Console with Clock with Random, Unit] = {
    for {
      serverStarted <- Promise.make[Nothing, SocketAddress]
      server <- echoServer(serverStarted, port)
      addr <- serverStarted.await
      //same          <- echoClient(addr)
    } yield server


    //    for {
    //      socketAddress <- SocketAddress.inetSocketAddress(port)
    //      channel <- DatagramChannel.bind(Some(socketAddress))
    //      _ <- putStrLn("Listening on port: " + port)
    //    } yield ()
    //
    //
    //    AsynchronousServerSocketChannel().use(
    //      server =>
    //        for {
    //          socketAddress <- SocketAddress.inetSocketAddress(port)
    //          _ <- server.bind(socketAddress)
    //          _ <- putStrLn("Listening on port: " + port)
    //          _ <- server.accept.reserve.flatMap(accept).forever
    //        } yield ()
    //    )
  }

  def echoServer(started: Promise[Nothing, SocketAddress], port: Int) = {
    //val jbuf = java.nio.ByteBuffer.allocate(10)
    //val bb = IO.effect(jbuf).map(Buffer.byteFromJava(_)).refineToOrDie[IllegalArgumentException]

    for {
      address <- SocketAddress.inetSocketAddress(port)
      _ <- putStrLn("Listening on port: " + port)
      sink    <- Buffer.byte(50)
      worker <- DatagramChannel
        .bind(Some(address))
        .use { server =>
          for {
            addr <- server.localAddress.flatMap(opt => IO.effect(opt.get).orDie)
            //_          <- started.succeed(addr)
            retAddress <- server.receive(sink)
            msg <- byteBuffer2String(sink)
            _ <- putStrLn("Received: " + msg) //StandardCharsets.UTF_8.decode(jbuf).toString())
            addr <- ZIO.fromOption(retAddress)
            _ <- sink.flip
            _ <- server.send(sink, addr)
          } yield ()
        }
        .fork
    } yield worker
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