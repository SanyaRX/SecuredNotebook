import cipher.AESCipher
import org.eclipse.jetty.server.Server
import service.{WebService, WebServiceBuilder}


object Main extends App {


  val server: Server = WebServiceBuilder.buildWebService(8080, classOf[WebService])
  server.start()


}