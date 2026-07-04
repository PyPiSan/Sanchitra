package com.pypisan.sanchitra.data.models
data class ProgramDisplayModel(
    val status: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val startTime: String,
    val endTime: String,
    val progress: Float
)

data class EpgScheduleWindow(
    val current: ProgramDisplayModel?,
    val previous: List<ProgramDisplayModel>,
    val next: List<ProgramDisplayModel>
)