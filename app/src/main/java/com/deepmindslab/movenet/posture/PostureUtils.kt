package com.deepmindslab.movenet.posture

import com.deepmindslab.movenet.data.ExerciseData
import com.deepmindslab.movenet.data.Person
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import com.deepmindslab.movenet.ratio.RatioTrackerUtils
import kotlin.math.abs
import kotlin.math.sqrt

class PostureUtils {

    companion object{
        fun postureExercise3(person: Person, exercise3Data: Exercise3Data):Boolean{
            var ratioOfWristsAndShouldersRight=false
            var ratioOfBodyWristRight= false
            var ratioOfElbowAndShouldersRight=false
            val ratioOfWristsAndShoulders=RatioTrackerUtils.calculateRatio(person,Pair(9,10),Pair(5,6))
            if (ratioOfWristsAndShoulders>exercise3Data.minRatioOfWristsAndShoulders && ratioOfWristsAndShoulders<exercise3Data.maxRatioOfWristsAndShoulders){
                ratioOfWristsAndShouldersRight=true
            }

            val middlePointOfHipsX=(person.keyPoints[11].coordinate.x+person.keyPoints[12].coordinate.x)/2
            val middlePointOfHipsY=(person.keyPoints[11].coordinate.y+person.keyPoints[12].coordinate.y)/2
            val noseX=person.keyPoints[0].coordinate.x
            val noseY=person.keyPoints[0].coordinate.y
            val difX= abs(middlePointOfHipsX-noseX)
            val difY= abs(middlePointOfHipsY-noseY)
            val distance1= sqrt(difX*difX+difY*difY)

            val rightShoulderX=person.keyPoints[5].coordinate.x
            val rightShoulderY= person.keyPoints[5].coordinate.y
            val rightWristX=person.keyPoints[9].coordinate.x
            val rightWristY=person.keyPoints[9].coordinate.y
            val difX1= abs(rightShoulderX-rightWristX)
            val difY1= abs(rightShoulderY-rightWristY)
            val distance2= sqrt(difX1*difX1+difY1*difY1)
            val ratioOfBodyWrist=distance2/distance1
            if (ratioOfBodyWrist<exercise3Data.maxRatioOfBodyWrist){
                ratioOfBodyWristRight=true
            }

            val ratioOfElbowAndShoulders=RatioTrackerUtils.calculateRatio(person,Pair(7,8),Pair(5,6))
            if (ratioOfElbowAndShoulders>exercise3Data.minRatioOfElbowAndShoulders && ratioOfElbowAndShoulders < exercise3Data.maxRatioOfElbowAndShoulders){
                ratioOfElbowAndShouldersRight=true
            }


            return if (ratioOfBodyWristRight && ratioOfWristsAndShouldersRight && ratioOfElbowAndShouldersRight){
                true
            }else{
                ExerciseData.errorMessage= "Posture is Not Correct"
                false
            }
        }
    }

}