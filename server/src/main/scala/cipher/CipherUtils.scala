package cipher

import java.math.BigInteger
import java.security.{KeyFactory, PublicKey}
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

import javax.crypto.Cipher

object CipherUtils {

  def encodeBase64ToString(bytes: Array[Byte]): String
  = java.util.Base64.getEncoder.encodeToString(bytes)


  def decodeBase64FromString(text: String)
  = java.util.Base64.getDecoder.decode(text)


  /**
    * TODO: implement encoding
    * */
  def publicFromString (publicK: String): PublicKey = {

    val keyBytes = Base64.getDecoder.decode(publicK.getBytes("utf-8"))

    val spec = new X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    val key = keyFactory.generatePublic(spec)

    key
  }


  def encryptRSA(plainText: String, key: String): String = {

    val publicKey = publicFromString(key)

    val encryptCipher = Cipher.getInstance("RSA")
    encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)

    val cipherText = encryptCipher.doFinal(plainText.getBytes("UTF_8"))

    Base64.getEncoder.encodeToString(cipherText)
  }


}
