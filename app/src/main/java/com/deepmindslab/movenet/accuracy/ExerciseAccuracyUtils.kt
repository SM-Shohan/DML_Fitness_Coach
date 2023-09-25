package com.deepmindslab.movenet.accuracy

import com.deepmindslab.movenet.resultdata.Exercise3ResultData
import com.deepmindslab.movenet.data.Person
import com.deepmindslab.movenet.ratio.RatioTrackerUtils
import kotlin.math.abs

class ExerciseAccuracyUtils {

    companion object{

        private var previousMaxRatio=0f
        private  var valueIncreasing=true

        fun calculateAccuracy(person: Person, exercise3ResultData: Exercise3ResultData):Float {
            val ratioOfRUpperAndLower= RatioTrackerUtils.calculateRatio(person,Pair(0,15),Pair(11,15))
            val absoluteDif:Float= abs(2.1f-ratioOfRUpperAndLower)
            var accuracy= 100-absoluteDif*300
            if (accuracy<0f){
                accuracy= 0f
            }
            calculateMaxAccuracy(ratioOfRUpperAndLower,exercise3ResultData)
            return accuracy
        }

        private fun calculateMaxAccuracy(ratioOfRUpperAndLower:Float, exercise3ResultData: Exercise3ResultData){
            if (ratioOfRUpperAndLower> previousMaxRatio){
                valueIncreasing=true
                previousMaxRatio=ratioOfRUpperAndLower
            }
            else{
                if (valueIncreasing){
                    val absoluteDif:Float= abs(2.1f-ratioOfRUpperAndLower)
                    val maxAccuracy= 100-absoluteDif*300
                    if (maxAccuracy>exercise3ResultData.maxAccuracy) {
                        exercise3ResultData.maxAccuracy = maxAccuracy
                    }
                }
                valueIncreasing=false
            }
        }
    }

}