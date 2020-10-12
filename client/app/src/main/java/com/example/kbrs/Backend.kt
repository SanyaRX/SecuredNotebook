package com.example.kbrs

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface Backend {
    @GET("/sessionKey")
    fun getSesionKey(@Query("publicKey")address: String): Deferred<SessionKey>

    @GET("/file")
    fun getFile(@Query("fileName")address: String = "file2.txt",
                @Query("sessionKey")key: String): Deferred<Text>

    @GET("/filesList")
    fun geListOfFiles(): Deferred<Files>
}