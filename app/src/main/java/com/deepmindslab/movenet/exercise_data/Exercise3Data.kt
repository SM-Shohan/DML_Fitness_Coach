package com.deepmindslab.movenet.exercise_data

import java.io.Serializable

data class Exercise3Data (
    val exerciseName:String,
    // Calculate the exercise is done or not
    val minRatioOfRUpperAndLower:Float,
    val maxRatioOfRUpperAndLower:Float,
    //calculate the accuracy
    val idealRatioOfRUpperAndLower:Float,
    //calculate the posture
    val minRatioOfWristsAndShoulders:Float,
    val maxRatioOfWristsAndShoulders:Float,
    val maxRatioOfBodyWrist:Float,
    val minRatioOfElbowAndShoulders:Float,
    val maxRatioOfElbowAndShoulders:Float
):Serializable