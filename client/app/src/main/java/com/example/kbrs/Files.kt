package com.example.kbrs

import com.google.gson.annotations.SerializedName

data class Files (
    @SerializedName("files") val files : List<String>
)