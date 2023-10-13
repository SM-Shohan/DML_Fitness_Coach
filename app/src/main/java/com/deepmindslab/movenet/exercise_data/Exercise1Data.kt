package com.deepmindslab.movenet.exercise_data

import java.io.Serializable

data class Exercise1Data(
    val exerciseName:String,
    val maxAngleShoulderWristUp:Float,
    val minAngleShoulderWristUp:Float,
    val maxAngleShoulderWristDown:Float,
    val minAngleShoulderWristDown:Float
    ):Serializable,ExerciseDataInterface
