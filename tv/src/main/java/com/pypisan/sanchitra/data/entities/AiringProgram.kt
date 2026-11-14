package com.pypisan.sanchitra.data.entities

data class ProgramDisplayModel(
    val status: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val startTime: String,
    val endTime: String,
    val progress: Float
)