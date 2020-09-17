import zio._
import zio.clock._
import zio.console._
import zio.random._
import zio.nio.channels.{AsynchronousServerSocketChannel, AsynchronousSocketChannel}
import zio.nio._
import zio.duration._
import zio.nio.core.SocketAddress

/*
  Testing on linux with:
  nc 127.0.0.1 PORTNUMBER
 */
object TCPServer {

  def start(port: Int): RIO[Console with Clock with Random, Unit] =
    AsynchronousServerSocketChannel().use(
      server =>
        for {
          socketAddress <- SocketAddress.inetSocketAddress(port)
          _ <- server.bind(socketAddress)
          _ <- putStrLn("Listening on port: " + port)
          _ <- server.accept.reserve.flatMap(accept).forever
        } yield ()
    )

  private def accept(reservation: Reservation[Any, Exception, AsynchronousSocketChannel]) =
    reservation.acquire.flatMap { socket =>
      socket.read(24, 30.seconds)
        .flatMap(chunk => putStrLn("Read chunk: " + chunk.mkString))
        .whenM(socket.isOpen)
        .forever
        .ensuring(reservation.release(Exit.Success(0)) *> putStrLn("Connection closed."))
        .catchAll(e => putStrLn("Connection closed due to: " + e.getMessage))
        .fork
    }
}