package com.deepmindslab.movenet.exercise_data

data class EvalExerciseProperty(
    val id: Int,
    val dayNumber: Int,
    val dayName: String,
    val holdInSeconds: Int,
    val repetitionInCount: Int,
    val setInCount: Int,
    val frequencyInDay: Int
)
