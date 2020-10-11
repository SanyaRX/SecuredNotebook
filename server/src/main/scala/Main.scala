import cipher.{AESCipher, CipherUtils}
import org.eclipse.jetty.server.Server
import service.{WebService, WebServiceBuilder}


object Main extends App {


  //val key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwapFhm5r9WVgNp15fb8MIQmsqV1LV9y+GqOlLZmEtvEK/ahzE+kyeTtsXIRE2pfFYPlQ/MZhtmBlXcZ8OxVMBAdmHmzbxsaWxKBKAkIo8D/er/GF5Ps3gcCRWxe4wqOUteW6fUiNlKQaUeqipCxdintBFcK1lWVXseQXL78jQQGcdCRQEuWIvCTTvoc7yok72yt5N8hsi2pTmjT1Igowol9fQ02K9SLaCdn9NJv4AeVPkeqN/NemrBeFZwpi3YO5JA2qtWrZPrAPErjub8T9i9X2EMqHIRrUhIZ5zJjCzCjpvP/Vmq/Q8Cs1v0o8mCkxPRcwLjNN9izPHXvCYXF3FQIDAQAB"

  //CipherUtils.decodeBase64FromString(key)

  val server: Server = WebServiceBuilder.buildWebService(8080, classOf[WebService])
  server.start()


}