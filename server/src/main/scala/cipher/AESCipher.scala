package cipher
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.{MessageDigest, SecureRandom}

class AESCipher {



  def encrypt(plainText: String, key: String): Array[Byte] = {
    val clean = plainText.getBytes
    // Generating IV.
    val ivSize = 16
    val iv = new Array[Byte](ivSize)
    val random = new SecureRandom()
    random.nextBytes(iv)
    val ivParameterSpec = new IvParameterSpec(iv)
    // Hashing key.
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(key.getBytes("UTF-8"))
    val keyBytes = new Array[Byte](16)
    System.arraycopy(digest.digest, 0, keyBytes, 0, keyBytes.length)
    val secretKeySpec = new SecretKeySpec(keyBytes, "AES")
    // Encrypt.
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
    val encrypted = cipher.doFinal(clean)
    // Combine IV and encrypted part.
    val encryptedIVAndText = new Array[Byte](ivSize + encrypted.length)
    System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize)
    System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length)
    encryptedIVAndText
  }


  def decrypt(encryptedIvTextBytes: Array[Byte], key: String): String = {
    val ivSize = 16
    val keySize = 16
    // Extract IV.
    val iv = new Array[Byte](ivSize)
    System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length)
    val ivParameterSpec = new IvParameterSpec(iv)
    // Extract encrypted part.
    val encryptedSize = encryptedIvTextBytes.length - ivSize
    val encryptedBytes = new Array[Byte](encryptedSize)
    System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize)
    // Hash key.
    val keyBytes = new Array[Byte](keySize)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(key.getBytes)
    System.arraycopy(md.digest, 0, keyBytes, 0, keyBytes.length)
    val secretKeySpec = new SecretKeySpec(keyBytes, "AES")
    // Decrypt.
    val cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
    val decrypted = cipherDecrypt.doFinal(encryptedBytes)
    new String(decrypted)
  }
}