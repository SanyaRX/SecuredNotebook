import cipher.{AESCipher, CipherUtils}
import org.eclipse.jetty.server.Server

object Main  {

  def main(args: Array[String]): Unit = {

    //val server: Server = WebServiceBuilder.buildWebService(8080, classOf[WebService])
    //server.start()

    val key = CipherUtils.generateStringKey(32)

    val cipher = new AESCipher()
    val openText = "Hello, world"
    println(s"Open text: $openText")

    val encryptedText = cipher.encrypt(openText, key)
    println(s"Encrypted text: ${new String(encryptedText)}")

    val decryptedText = cipher.decrypt(encryptedText, key)
    println(s"Decrypted text: $decryptedText")

    //println(CipherUtils.generateStringKey(32))
  }

}

