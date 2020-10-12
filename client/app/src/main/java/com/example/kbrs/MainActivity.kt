package com.example.kbrs

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets.UTF_8
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64.getDecoder
import java.util.Base64.getEncoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


val RSA = "RSA/ECB/OAEPWithSHA1AndMGF1Padding"

class MainActivity : AppCompatActivity() {

    val PRIVATE_KEY = "PRIVATE_KEY"
    val PUBLIC_KEY = "PUBLIC_KEY"

    lateinit var pair: KeyPair
    lateinit var privateKey: PrivateKey
    lateinit var publicKey: PublicKey

    lateinit var key: String
    lateinit var sessionKey: String


    lateinit var sPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = FileAdapter{
            GlobalScope.launch(Dispatchers.IO) {
                val text = Apifactory.api.getFile(it, sessionKey).await()
                withContext(Dispatchers.Main) {
                    if(text.text != "Error 403") {
                        val intent = Intent(applicationContext, DetailActivity::class.java)
                        val send = decrypt(text.text, key)
                        intent.putExtra("KEY", "$send")
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext,
                            "Token time is up. Generate a new one", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }

        sPref = getPreferences(Context.MODE_PRIVATE)
        getOldKeys()

        rv.adapter = adapter
        GlobalScope.launch(Dispatchers.IO) {
            val responseBody = Apifactory.api
                .getSesionKey(getEncoder().encodeToString(publicKey.encoded)).await()

            sessionKey = responseBody.sessionKey
            key = decrypt(sessionKey, privateKey)!!
        }

        button.setOnClickListener {
            generate()
            GlobalScope.launch(Dispatchers.IO) {
                val responseBody = Apifactory.api
                    .getSesionKey(getEncoder().encodeToString(publicKey.encoded)).await()

                sessionKey = responseBody.sessionKey
                key = decrypt(sessionKey, privateKey)!!
            }
        }

        showProgress()
        GlobalScope.launch(Dispatchers.IO) {
            val files = Apifactory.api.geListOfFiles().await()
            withContext(Dispatchers.Main) {
                adapter.submitList(files.files.map { FileModel(it) })
                hideProgress()
            }
        }
    }



    private fun publicFromString (publicK: String): PublicKey {
        val keyBytes: ByteArray = getDecoder().decode(publicK.toByteArray())
        val spec =
            X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec)
    }

    private fun privateFromString (privateK: String): PrivateKey {
        val keyBytes: ByteArray = getDecoder().decode(privateK.toByteArray())
        val keySpec =
            PKCS8EncodedKeySpec(keyBytes)
        val fact = KeyFactory.getInstance("RSA")
        return fact.generatePrivate(keySpec)
    }

    private fun generate(){
        pair = generateKeyPair()!!

        publicKey = pair.public
        privateKey = pair.private
        val ed = sPref.edit()

        ed.putString(PRIVATE_KEY, getEncoder().encodeToString(pair.private.encoded))
        ed.putString(PUBLIC_KEY, getEncoder().encodeToString(pair.public.encoded))

        ed.apply()

        Toast.makeText(applicationContext, "New Keys Generated", Toast.LENGTH_LONG).show()
    }


    fun encrypt(plainText: String, publicKey: PublicKey?): String? {
        val encryptCipher = Cipher.getInstance(RSA)
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val cipherText = encryptCipher.doFinal(plainText.toByteArray(UTF_8))
        return getEncoder().encodeToString(cipherText)
    }


    fun decrypt(cipherText: String?, privateKey: PrivateKey?): String? {
        val bytes: ByteArray = getDecoder().decode(cipherText)
        val decriptCipher = Cipher.getInstance(RSA)
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(decriptCipher.doFinal(bytes), UTF_8)
    }




    private fun getOldKeys() {
        sPref.getString(PRIVATE_KEY, "")?.let{
            if(it != "") {
                privateKey = privateFromString(it)
            }
        }

        sPref.getString(PUBLIC_KEY, "")?.let{
            if(it != "") {
                publicKey = publicFromString(it)
            }
        }

    }


    fun generateKeyPair(): KeyPair? {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048)
        return generator.generateKeyPair()
    }


    private fun decrypt(encryptedIvText: String, key: String): String {
        val ivSize = 16

        val encryptedIvTextBytes = getDecoder().decode(encryptedIvText)
        // Extract IV.
        val iv = ByteArray(ivSize)
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.size)
        val ivParameterSpec = IvParameterSpec(iv)
        // Extract encrypted part.
        val encryptedSize: Int = encryptedIvTextBytes.size - ivSize
        val encryptedBytes = ByteArray(encryptedSize)
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize)
        // Hash key.
        val keyBytes = ByteArray(key.length)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(key.toByteArray())
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.size)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Decrypt.

        // Decrypt.
        val cipherDecrypt =
            Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decrypted = cipherDecrypt.doFinal(encryptedBytes)
        return String(decrypted)
    }

    private fun showProgress() {
        loading.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        loading.visibility = View.INVISIBLE
    }
}