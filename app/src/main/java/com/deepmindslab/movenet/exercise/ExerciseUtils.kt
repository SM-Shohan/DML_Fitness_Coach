package com.deepmindslab.movenet.exercise

import com.deepmindslab.movenet.angle.AnglesTrackerUtils
import com.deepmindslab.movenet.resultdata.Exercise3ResultData
import com.deepmindslab.movenet.data.Person
import com.deepmindslab.movenet.data.ExerciseData
import com.deepmindslab.movenet.ratio.RatioTrackerUtils

class ExerciseUtils {

    companion object{
        private var updateUp=false
        private var updateDown=false

        fun countExercise1Movement(person: Person){
            val angleL:Float? = AnglesTrackerUtils.calculateAngle(person,5)
            val angleR:Float? = AnglesTrackerUtils.calculateAngle(person,6)

            if (angleL != null) {
                if (angleR != null) {
                    if((angleL > 310f) && (angleR > 310f)){
                        if (!updateUp) {
                            updateUp=true
                            updateDown=false
                        }
                    }
                }
            }
            if (angleL != null) {
                if (angleR != null) {
                    if((angleL < 280f) && (angleR < 280f)){
                        if (!updateDown) {
                            ExerciseData.exercise1Counter++
                            updateDown=true
                            updateUp=false
                        }
                    }
                }
            }
        }
        fun countExercise2Movement(person: Person){
            val angleL: Float =AnglesTrackerUtils.calculateAngle(person, 9,7,5)

            if (angleL<60){
                if (!updateUp) {
                    ExerciseData.exercise2Counter++
                    if (ExerciseData.exercise2Counter==1){
                        ExerciseData.exerciseStartTime=System.currentTimeMillis()
                    }
                    else if(ExerciseData.exercise2Counter>1){
                        ExerciseData.exerciseEndTime=System.currentTimeMillis()
                        ExerciseData.exerciseTime=ExerciseData.exerciseEndTime-ExerciseData.exerciseStartTime
                        ExerciseData.exerciseStartTime=ExerciseData.exerciseEndTime
                        if (ExerciseData.exerciseTime>5000 || ExerciseData.exerciseTime<1000){
                            ExerciseData.exercise2Counter--
                        }
                    }
                    updateUp=true
                    updateDown=false
                }
            }
            if (angleL>160){
                if (!updateDown) {
                    updateDown=true
                    updateUp=false
                }
            }
        }

        fun trackExercise3(person: Person, exercise3ResultData: Exercise3ResultData):Float{
            val ratioOfRUpperAndLower=RatioTrackerUtils.calculateRatio(person,Pair(0,15),Pair(11,15))
            if (ratioOfRUpperAndLower<1.85){
                if (!updateUp){
                    updateUp=true
                    updateDown=false
                }
            }
            if(ratioOfRUpperAndLower>2.05){
                if (!updateDown) {
                    ExerciseData.exerciseCounter++
                    exercise3ResultData.numberOfExercise=ExerciseData.exerciseCounter
                    updateDown=true
                    updateUp=false
                }
            }
            return ratioOfRUpperAndLower
        }


    }

}