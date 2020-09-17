
import zio._
import zio.nio.channels.DatagramChannel
import zio.nio.core.{SocketAddress, ByteBuffer}
import zio.console._

object Main extends zio.App {
  def run(args: List[String]) = {
    //udpClient()
    //ZIO.effectTotal(println("Finished Running")).exitCode

//        UDPServer.start(9999)
//          .foldM(e => putStrLn("Error: " + e.getMessage) *> ZIO.succeed(ExitCode.failure), _ => ZIO.succeed(ExitCode.success))
    (UDPServer.start(9999)as ExitCode.success) orElse ZIO.succeed(ExitCode.success)

  }


  //DOES NOT WORK
  def udpClient(): Unit = {
    val jbuf = java.nio.ByteBuffer.allocate(10).put("AAAAAAAAAA".getBytes())
    val buff = zio.nio.core.Buffer.byteFromJava(jbuf)

    for {
      serverAddress <- SocketAddress.inetSocketAddress("localhost", 9999)
      udpChannel <- DatagramChannel.connect(serverAddress).use { channel =>
        println("SENDING: " + buff.toString)
        channel.write(buff)
      }.fork
    } yield ()

  }

}


