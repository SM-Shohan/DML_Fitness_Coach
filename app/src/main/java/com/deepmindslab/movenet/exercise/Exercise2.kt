package com.deepmindslab.movenet.exercise

import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise_data.Exercise2Data
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.exercise_utility_classes.AnglesTrackerUtils
import com.deepmindslab.movenet.exercise_utility_classes.StandingUtils
import com.deepmindslab.movenet.result_data.ExerciseResultData
import java.io.Serializable

class Exercise2:Exercise,Serializable {

    // This variable are used to track the up-down body part movement
    private var updateUp=false
    private var updateDown=false
    private var exerciseCounter=0

    // This variables are used to track exercise time
    private var exerciseStartTime:Long=0
    private var exerciseEndTime:Long=0
    private var exerciseTime:Long=0

    //This variables are used to track pre-exercise validity
    private var errorCountE2RW=0
    private var errorCountE2RE=0
    private var errorCountE2RS=0

    override fun calculateMaxAccuracy(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise2Data=data as Exercise2Data
        //methods will be implemented soon
        exerciseResult.maxAccuracy=0.0f
    }

    override fun calculateCurrentAccuracy(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise2Data=data as Exercise2Data
        //methods will be implemented soon
        exerciseResult.currentAccuracy=0.0f
    }

    override fun numberOfIteration(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise2Data=data as Exercise2Data
        val angleRightEl: Float = AnglesTrackerUtils.calculateAngle(person, 9,7,5)

        if (angleRightEl<exercise2Data.minAngleRightElbow){
            if (!updateUp) {
                exerciseCounter++
                exerciseResult.numberOfIteration=exerciseCounter
                if (exerciseResult.numberOfIteration==1){
                    exerciseStartTime=System.currentTimeMillis()
                }
                else if(exerciseResult.numberOfIteration>1){
                    exerciseEndTime=System.currentTimeMillis()
                    exerciseTime= exerciseEndTime- exerciseStartTime
                    exerciseResult.timePerIteration=exerciseTime
                    exerciseStartTime= exerciseEndTime
                    if (exerciseTime>exercise2Data.maxFreqInMs || exerciseTime<exercise2Data.minFreqInMs){
                        exerciseCounter--
                        exerciseResult.numberOfIteration=exerciseCounter
                    }
                }
                updateUp =true
                updateDown =false
            }
        }
        if (angleRightEl>exercise2Data.maxAngleRightElbow){
            if (!updateDown) {
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
        val exercise2Data=data as Exercise2Data
        return true
    }

    override fun exerciseValidity(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResultData: ExerciseResultData
    ): Boolean {
        val exercise2Data=data as Exercise2Data
        if (person.keyPoints[5].score<0.25){
            errorCountE2RE =0
            errorCountE2RW =0
            if (errorCountE2RS >20){
                exerciseResultData.exerciseErrorMessage = "Can't see your right shoulder"
                return  false
            }else{
                errorCountE2RS++
            }
        }
        else if (person.keyPoints[7].score<0.25){

            errorCountE2RS =0
            errorCountE2RW =0
            if (errorCountE2RE >20){
                exerciseResultData.exerciseErrorMessage = "Can't see your right Elbow"
                return  false
            }else{
                errorCountE2RE++
            }
        }
        else if (person.keyPoints[9].score<0.25){
            errorCountE2RE =0
            errorCountE2RS =0
            if (errorCountE2RW >20){
                exerciseResultData.exerciseErrorMessage = "Can't see your right Wrist"
                return  false
            }else{
                errorCountE2RW++
            }
        }

        if (StandingUtils.standingPosition(person) != 1) {
            exerciseResultData.exerciseErrorMessage= "Stand left facing"
            return false
        }

        return  true
    }
}