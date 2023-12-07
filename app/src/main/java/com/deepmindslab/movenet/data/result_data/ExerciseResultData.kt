package com.deepmindslab.movenet.data.result_data

import java.io.Serializable

class ExerciseResultData:Serializable {

    var maxAccuracy:Float= 0.0F
    var numberOfIteration:Int=0
    var timePerIteration:Long=0
    var currentAccuracy=0.0F

    var exerciseErrorMessage:String=""
    var exerciseValidity=false

}