package cipher


import java.security.{MessageDigest, SecureRandom}
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}


object AESCipher {


  def generateKey(): String = {

    val r = new scala.util.Random
    val sb = new StringBuilder

    for (i <- 1 to 16) sb.append(r.nextPrintableChar)

    sb.toString

  }


  def encrypt(plainText: String, key: String): String = {

    val clean = plainText.getBytes

    val ivSize = 16
    val iv = new Array[Byte](ivSize)
    val random = new SecureRandom()
    random.nextBytes(iv)
    val ivParameterSpec = new IvParameterSpec(iv)

    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(key.getBytes("UTF-8"))
    val keyBytes = new Array[Byte](16)
    System.arraycopy(digest.digest, 0, keyBytes, 0, keyBytes.length)
    val secretKeySpec = new SecretKeySpec(keyBytes, "AES")

    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
    val encrypted = cipher.doFinal(clean)

    val encryptedIVAndText = new Array[Byte](ivSize + encrypted.length)
    System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize)
    System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length)


    CipherUtils.encodeBase64ToString(encryptedIVAndText)

  }

  def decrypt(encryptedIvText: String, key: String): String = {

    val ivSize = 16

    val encryptedIvTextBytes = CipherUtils.decodeBase64FromString(encryptedIvText)

    val iv = new Array[Byte](ivSize)
    System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length)
    val ivParameterSpec = new IvParameterSpec(iv)

    val encryptedSize = encryptedIvTextBytes.length - ivSize
    val encryptedBytes = new Array[Byte](encryptedSize)
    System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize)

    val keyBytes = new Array[Byte](key.length)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(key.getBytes)
    System.arraycopy(md.digest, 0, keyBytes, 0, keyBytes.length)
    val secretKeySpec = new SecretKeySpec(keyBytes, "AES")
    val cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
    val decrypted = cipherDecrypt.doFinal(encryptedBytes)
    new String(decrypted)

  }

}
