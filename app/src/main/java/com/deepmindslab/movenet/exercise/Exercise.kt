package com.deepmindslab.movenet.exercise


import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.result_data.ExerciseResultData

interface Exercise {
    fun calculateMaxAccuracy(person: Person, data: ExerciseDataInterface, exerciseResult:ExerciseResultData)
    fun calculateCurrentAccuracy(person: Person, data: ExerciseDataInterface, exerciseResult:ExerciseResultData)
    fun numberOfIteration(person: Person, data: ExerciseDataInterface, exerciseResult:ExerciseResultData)


    //Pre exercise functions. Run before exercise Starts
    fun preExercisePosture(person: Person, data: ExerciseDataInterface, exerciseResultData: ExerciseResultData):Boolean
    fun exerciseValidity(person: Person, data: ExerciseDataInterface, exerciseResultData: ExerciseResultData):Boolean

}