package com.deepmindslab.movenet.exercise_data

import java.io.Serializable

data class Exercise2Data(
    val exerciseName:String,
    val minAngleRightElbow :Float,
    val maxAngleRightElbow :Float,
    val minFreqInMs:Float,
    val maxFreqInMs:Float,
):Serializable,ExerciseDataInterface
