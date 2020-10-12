package service

import config.ServerConfig
import org.scalatra.ScalatraServlet
import java.io.File

import cipher.{AESCipher, CipherUtils, SessionKey}

import scala.io.Source

class WebService extends ScalatraServlet  {


  get("/filesList") {

    val fileDirectory = new File(ServerConfig.fileDirectoryPath)

    val sb = new StringBuilder()

    sb.append("{ \"files\": [")

    if (fileDirectory.exists() && fileDirectory.isDirectory) {

      sb.append(fileDirectory.listFiles(_.isFile())
                   .map("\"%s\"" format _.getName)
                   .filter(!_.endsWith(".jar"))
                   .mkString(", ")
      )
    }

    sb.append("] }")

    sb.toString
  }


  get("/sessionKey") {

    val publicKeyString = params.get("publicKey").get.replace(" ", "+").replace("\n", "")

    val sessionKey = new SessionKey(ServerConfig.sessionKeyLifeTimeMills)

    val encryptedSessionKey = CipherUtils.encryptRSA(sessionKey.getSessionKey, publicKeyString)
   
    servletContext.setAttribute("sessionKey:" + encryptedSessionKey, sessionKey)

    "{\"sessionKey\":\"%s\"}" format encryptedSessionKey

  }


  get("/file") {

    val fileName = params.get("fileName").get
    val encryptedSessionKey = params.get("sessionKey").get.replace(" ", "+").replace("\n", "")

    if (encryptedSessionKey == null) null

    val file = new File(ServerConfig.fileDirectoryPath + s"/$fileName")

    if (!file.exists) null

    val fileText = Source.fromFile(file).getLines.mkString

    val sessionKey = servletContext.getAttribute("sessionKey:" + encryptedSessionKey).asInstanceOf[SessionKey]

    var response: String = ""

    if (!sessionKey.isAlive) response = "{\"text\":\"Error 403\"}"
    else {
      val encryptedText = AESCipher.encrypt(fileText, sessionKey.getSessionKey)

      response = "{\"text\":\"%s\"}" format encryptedText
    }

    response
  }




}
