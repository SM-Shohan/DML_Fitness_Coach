package com.deepmindslab.movenet.exercise_utility_classes

import com.deepmindslab.movenet.body_parts_detection_data.Person
import kotlin.math.sqrt

class RatioTrackerUtils {

    companion object{
        fun calculateRatio(person: Person, pairOfCoordinate1:Pair<Int,Int>, pairOfCoordinate2:Pair<Int,Int>):Float{
            val difX1=person.keyPoints[pairOfCoordinate1.first].coordinate.x-person.keyPoints[pairOfCoordinate1.second].coordinate.x
            val difY1=person.keyPoints[pairOfCoordinate1.first].coordinate.y-person.keyPoints[pairOfCoordinate1.second].coordinate.y
            val distance1= sqrt(difX1*difX1+difY1*difY1)

            val difX2=person.keyPoints[pairOfCoordinate2.first].coordinate.x-person.keyPoints[pairOfCoordinate2.second].coordinate.x
            val difY2=person.keyPoints[pairOfCoordinate2.first].coordinate.y-person.keyPoints[pairOfCoordinate2.second].coordinate.y
            val distance2= sqrt(difX2*difX2+difY2*difY2)

            return distance1/distance2
        }
    }

}