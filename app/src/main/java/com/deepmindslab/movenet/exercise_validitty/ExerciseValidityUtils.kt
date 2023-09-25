package com.deepmindslab.movenet.exercise_validitty

import com.deepmindslab.movenet.data.Person
import com.deepmindslab.movenet.data.ExerciseData

class ExerciseValidityUtils {

    companion object{
        private var errorCountLS=0
        private var errorCountRS=0
        private var errorCountLEl=0
        private var errorCountREl=0
        private var errorCountLH=0
        private var errorCountRH=0
        fun exercise1Validity(person:Person):Boolean{
            person.keyPoints.forEach { keyPoint ->
                if (keyPoint.bodyPart.position==5 && keyPoint.score<0.25){
                    errorCountLS++
                    errorCountRS=0
                    errorCountLEl=0
                    errorCountREl=0
                    errorCountLH=0
                    errorCountRH=0
                    if (errorCountLS>20){
                        ExerciseData.errorMessage = "Can't see your left shoulder"
                        return false
                    }
                }
                else if (keyPoint.bodyPart.position==6 && keyPoint.score<0.25){
                    errorCountLS=0
                    errorCountLEl=0
                    errorCountREl=0
                    errorCountLH=0
                    errorCountRH=0
                    errorCountRS++
                    if (errorCountRS>20){
                        ExerciseData.errorMessage = "Can't see your right shoulder"
                        return  false
                    }
                }
                else if (keyPoint.bodyPart.position==7 && keyPoint.score<0.25){
                    errorCountLEl++
                    errorCountLS=0
                    errorCountRS=0
                    errorCountREl=0
                    errorCountLH=0
                    errorCountRH=0
                    if (errorCountLEl>20){
                        ExerciseData.errorMessage = "Can't see your left elbow"
                        return  false
                    }

                }
                else if (keyPoint.bodyPart.position==8 && keyPoint.score<0.25){
                    errorCountREl++
                    errorCountLS=0
                    errorCountRS=0
                    errorCountLEl=0
                    errorCountLH=0
                    errorCountRH=0
                    if (errorCountREl>20){
                        ExerciseData.errorMessage = "Can't see your right elbow"
                        return  false
                    }
                }
                else if (keyPoint.bodyPart.position==11 && keyPoint.score<0.25){
                    errorCountLH++
                    errorCountLS=0
                    errorCountRS=0
                    errorCountLEl=0
                    errorCountREl=0
                    errorCountRH=0
                    if (errorCountLH>20){
                        ExerciseData.errorMessage = "Can't see your left hip"
                        return  false
                    }

                }
                else if (keyPoint.bodyPart.position==5 && keyPoint.score<0.25){
                    errorCountRH++
                    errorCountLS=0
                    errorCountRS=0
                    errorCountLEl=0
                    errorCountREl=0
                    errorCountLH=0
                    if (errorCountRH>20){
                        ExerciseData.errorMessage = "Can't see your left hip"
                        return  false
                    }

                }
            }
            return  true
        }


        private var errorCountE2RW=0
        private var errorCountE2RE=0
        private var errorCountE2RS=0
        fun exercise2Validity(person: Person):Boolean{
            if (person.keyPoints[5].score<0.25){
                errorCountE2RE=0
                errorCountE2RW=0
                if (errorCountE2RS>20){
                    ExerciseData.errorMessage = "Can't see your right shoulder"
                    return  false
                }else{
                    errorCountE2RS++
                }
            }
            else if (person.keyPoints[7].score<0.25){

                errorCountE2RS=0
                errorCountE2RW=0
                if (errorCountE2RE>20){
                    ExerciseData.errorMessage = "Can't see your right Elbow"
                    return  false
                }else{
                    errorCountE2RE++
                }
            }
            else if (person.keyPoints[9].score<0.25){
                errorCountE2RE=0
                errorCountE2RS=0
                if (errorCountE2RW>20){
                    ExerciseData.errorMessage = "Can't see your right Wrist"
                    return  false
                }else{
                    errorCountE2RW++
                }
            }
            return  true
        }


        fun exercise3Validity(person: Person):Boolean{
            person.keyPoints.forEach { keyPoint ->
                if (keyPoint.score<0.0){
                    ExerciseData.errorMessage = "Can't see your full body"
                    return  false
                }
            }
            return  true
        }

    }

}