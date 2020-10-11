package service

import config.ServerConfig
import org.scalatra.ScalatraServlet
import java.io.File
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.spec.RSAPublicKeySpec

import cipher.{AESCipher, CipherUtils}
import javax.crypto.Cipher

import scala.io.Source

class WebService extends ScalatraServlet  {

  /**
  * TODO: JSON response
  * */
  get("/filesList") {

    val fileDirectory = new File(ServerConfig.fileDirectoryPath)

    if (fileDirectory.exists() && fileDirectory.isDirectory) {

      fileDirectory.listFiles(_.isFile())
                   .map(_.getName)
                   .mkString("\n")
    } else {
      null
    }
  }


  get("/sessionKey") {


    val publicKeyString = params.get("publicKey").get.replace(" ", "+")

    val sessionKey = AESCipher.generateSessionKey()

    val encryptedSessionKey = CipherUtils.encryptRSA(sessionKey, publicKeyString)

    servletContext.setAttribute("sessionKey:" + encryptedSessionKey, sessionKey)

    "{\"sessionKey\":\"%s\"}" format encryptedSessionKey

  }


  get("/file") {

    val fileName = params.get("fileName").get
    val encryptedSessionKey = params.get("sessionKey").orNull

    if (encryptedSessionKey == null) null

    val file = new File(ServerConfig.fileDirectoryPath + s"/$fileName")

    if (!file.exists) null

    val fileText = Source.fromFile(file).getLines.mkString

    val sessionKey = servletContext.getAttribute("sessionKey:" + encryptedSessionKey).toString

    val encryptedText = AESCipher.encrypt(fileText, sessionKey)


    "{\"text\":\"%s\"}" format encryptedText
    (encryptedSessionKey, sessionKey)
  }




}