package com.deepmindslab.movenet.exercise

import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise_data.Exercise1Data
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.exercise_utility_classes.AnglesTrackerUtils
import com.deepmindslab.movenet.exercise_utility_classes.StandingUtils
import com.deepmindslab.movenet.data.result_data.ExerciseResultData
import java.io.Serializable

class Exercise1:Exercise,Serializable {

    //this variable use by override fun numberOfIteration
    private var exerciseCounter=0
    private var updateUp=false
    private var updateDown=false

    override fun calculateMaxAccuracy(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise1Data=data as Exercise1Data
    }

    override fun calculateCurrentAccuracy(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise1Data=data as Exercise1Data
    }

    override fun numberOfIteration(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResult: ExerciseResultData
    ) {
        val exercise1Data=data as Exercise1Data

        val angleShoulderWristL:Float = AnglesTrackerUtils.calculateAngle(person,12,6,10)
        val angleShoulderWristR: Float = AnglesTrackerUtils.calculateAngle(person,9,5,11)

        if((angleShoulderWristL > exercise1Data.minAngleShoulderWristUp && angleShoulderWristR > exercise1Data.minAngleShoulderWristUp)
            && (angleShoulderWristL < exercise1Data.maxAngleShoulderWristUp && angleShoulderWristR < exercise1Data.maxAngleShoulderWristUp)){
            if (!updateUp && updateDown) {
                exerciseCounter++
                exerciseResult.numberOfIteration=exerciseCounter
                updateUp =true
                updateDown =false
            }
        }else if ((angleShoulderWristL > exercise1Data.maxAngleShoulderWristUp || angleShoulderWristR > exercise1Data.maxAngleShoulderWristUp)){
            if (updateUp)
            {
                updateUp=false
            }
        }else if((angleShoulderWristL < exercise1Data.maxAngleShoulderWristDown && angleShoulderWristR < exercise1Data.maxAngleShoulderWristDown)
            && (angleShoulderWristL > exercise1Data.minAngleShoulderWristDown && angleShoulderWristR > exercise1Data.minAngleShoulderWristDown)){
            if (!updateDown) {
                updateDown =true
                updateUp =false
            }
        }else if((angleShoulderWristL < exercise1Data.minAngleShoulderWristDown && angleShoulderWristR < exercise1Data.minAngleShoulderWristDown)){
            if (updateDown){
                updateDown=false
            }
        }

    }

    override fun preExercisePosture(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResultData: ExerciseResultData
    ): Boolean {
        val exercise1Data=data as Exercise1Data
        return true
    }

    override fun exerciseValidity(
        person: Person,
        data: ExerciseDataInterface,
        exerciseResultData: ExerciseResultData
    ): Boolean {
        val exercise1Data=data as Exercise1Data
        if (StandingUtils.standingPosition(person) != 3){
            return false
        }
        return true
    }
}