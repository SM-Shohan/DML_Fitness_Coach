package com.deepmindslab.movenet.standing

import com.deepmindslab.movenet.data.Person

class StandingUtils {

    companion object{
        private var countLeft=0
        private var countRight=0
        private var countFront=0
        private var countBack=0
        fun standingPosition(person: Person):Int{

            val leftShoulderDistanceX=person.keyPoints[6].coordinate.x-person.keyPoints[0].coordinate.x
            val rightShoulderDistanceX=person.keyPoints[5].coordinate.x-person.keyPoints[0].coordinate.x
            if (leftShoulderDistanceX>0 && rightShoulderDistanceX >0){
                countLeft++
            }
            else if (leftShoulderDistanceX<0 && rightShoulderDistanceX<0){
                countRight++
            }
            else if (leftShoulderDistanceX<0 && rightShoulderDistanceX>0){
                countFront++
            }
            else if (leftShoulderDistanceX>0 && rightShoulderDistanceX<0){
                countBack++
            }

            if (countLeft>20){
                countLeft--
                countRight=0
                countBack=0
                countFront=0
                return 1
            }
            else if (countRight>20){
                countRight--
                countLeft=0
                countBack=0
                countFront=0
                return 2
            }
            else if (countFront>20){
                countFront--
                countLeft=0
                countBack=0
                countRight=0
                return 3
            }
            else if (countBack>20){
                countBack--
                countLeft=0
                countFront=0
                countRight=0
                return 4
            }
            return -1
        }



    }

}