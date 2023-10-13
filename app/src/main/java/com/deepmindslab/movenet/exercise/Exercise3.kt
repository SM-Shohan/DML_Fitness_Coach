package com.deepmindslab.movenet.exercise

import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.exercise_utility_classes.RatioTrackerUtils
import com.deepmindslab.movenet.exercise_utility_classes.StandingUtils
import com.deepmindslab.movenet.result_data.ExerciseResultData
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

class Exercise3:Exercise,Serializable {

    //this variable used by override fun calculateMaxAccuracy
    private var previousMaxRatio=0f

    //this variable used by override fun numberOfIteration
    private var exerciseCounter=0
    private var updateUp=false
    private var updateDown=false

    //this variable used by override fun calculateMaxAccuracy
    private  var valueIncreasing=true

    override fun calculateMaxAccuracy(person: Person, data: ExerciseDataInterface, exerciseResult: ExerciseResultData){
        val exercise3Data=data as Exercise3Data
        val ratioOfRUpperAndLower= RatioTrackerUtils.calculateRatio(person,Pair(0,15),Pair(11,15))
        if (ratioOfRUpperAndLower> previousMaxRatio){
            valueIncreasing =true
            previousMaxRatio =ratioOfRUpperAndLower
        }
        else{
            if (valueIncreasing){
                val absoluteDif:Float= abs(exercise3Data.idealRatioOfRUpperAndLower-ratioOfRUpperAndLower)
                val maxAccuracy= 100-absoluteDif*300
                if (maxAccuracy>exerciseResult.maxAccuracy) {
                    exerciseResult.maxAccuracy = maxAccuracy
                }
            }
            valueIncreasing =false
        }
    }

    override fun calculateCurrentAccuracy(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise3Data=data as Exercise3Data
        val ratioOfRUpperAndLower= RatioTrackerUtils.calculateRatio(person,Pair(0,15),Pair(11,15))
        val absoluteDif:Float= abs(exercise3Data.idealRatioOfRUpperAndLower-ratioOfRUpperAndLower)
        var accuracy= 100-absoluteDif*300
        if (accuracy<0f){
            accuracy= 0f
        }
        exerciseResult.currentAccuracy=accuracy
    }

    override fun numberOfIteration(person: Person, data:ExerciseDataInterface, exerciseResult: ExerciseResultData) {
        val exercise3Data=data as Exercise3Data
        val ratioOfRUpperAndLower= RatioTrackerUtils.calculateRatio(person,Pair(0,15),Pair(11,15))
        if (ratioOfRUpperAndLower<exercise3Data.minRatioOfRUpperAndLower){
            if (!updateUp){
                updateUp =true
                updateDown =false
            }
        }
        if(ratioOfRUpperAndLower>exercise3Data.maxRatioOfRUpperAndLower){
            if (!updateDown) {
                exerciseCounter++
                exerciseResult.numberOfIteration= exerciseCounter
                updateDown =true
                updateUp =false
            }
        }
    }

    override fun preExercisePosture(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResultData: ExerciseResultData
    ): Boolean {
        val exercise3Data= data as Exercise3Data
        var ratioOfWristsAndShouldersRight=false
        var ratioOfBodyWristRight= false
        var ratioOfElbowAndShouldersRight=false
        val ratioOfWristsAndShoulders=
            RatioTrackerUtils.calculateRatio(person,Pair(9,10),Pair(5,6))
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

        val ratioOfElbowAndShoulders= RatioTrackerUtils.calculateRatio(person,Pair(7,8),Pair(5,6))
        if (ratioOfElbowAndShoulders>exercise3Data.minRatioOfElbowAndShoulders && ratioOfElbowAndShoulders < exercise3Data.maxRatioOfElbowAndShoulders){
            ratioOfElbowAndShouldersRight=true
        }


        return if (ratioOfBodyWristRight && ratioOfWristsAndShouldersRight && ratioOfElbowAndShouldersRight){
            true
        }else{
            exerciseResultData.exerciseErrorMessage= "Posture is Not Correct"
            false
        }
    }

    override fun exerciseValidity(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResultData: ExerciseResultData
    ): Boolean {
        val exercise3Data=data as Exercise3Data
        person.keyPoints.forEach { keyPoint ->
            if (keyPoint.score<0.0){
                exerciseResultData.exerciseErrorMessage = "Can't see your full body"
                return  false
            }
        }
        if (StandingUtils.standingPosition(person) != 3) {
            exerciseResultData.exerciseErrorMessage= "Stand front facing"
            return false
        }
        if (!preExercisePosture(person,exercise3Data,exerciseResultData)){
            return  false
        }
        return  true
    }

}