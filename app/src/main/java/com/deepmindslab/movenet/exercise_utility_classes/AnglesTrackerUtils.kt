package com.deepmindslab.movenet.exercise_utility_classes

import android.graphics.PointF
import com.deepmindslab.movenet.body_parts_detection_data.Person
import kotlin.math.atan2

class AnglesTrackerUtils {

    companion object{
        fun calculateAngle(person: Person, jointNum: Int):Float?{
            var coordinate1: PointF? =null
            var coordinate2: PointF? =null
            var coordinate3: PointF? =null
            person.keyPoints.forEach { keyPoint ->
                when (jointNum) {
                    7 -> {
                        when (keyPoint.bodyPart.position) {
                            5 -> {
                                coordinate1 = keyPoint.coordinate
                            }

                            7 -> {
                                coordinate2 = keyPoint.coordinate
                            }

                            9 -> {
                                coordinate3 = keyPoint.coordinate
                            }
                        }
                    }
                    5 -> {
                        when (keyPoint.bodyPart.position) {
                            7 -> {
                                coordinate3 = keyPoint.coordinate
                            }

                            5 -> {
                                coordinate2 = keyPoint.coordinate
                            }

                            11 -> {
                                coordinate1 = keyPoint.coordinate
                            }
                        }
                    }
                    8 -> {
                        when (keyPoint.bodyPart.position) {
                            6 -> {
                                coordinate3 = keyPoint.coordinate
                            }

                            8 -> {
                                coordinate2 = keyPoint.coordinate
                            }

                            10 -> {
                                coordinate1 = keyPoint.coordinate
                            }
                        }
                    }
                    6 -> {
                        when (keyPoint.bodyPart.position) {
                            8 -> {
                                coordinate1 = keyPoint.coordinate
                            }

                            6 -> {
                                coordinate2 = keyPoint.coordinate
                            }

                            12 -> {
                                coordinate3 = keyPoint.coordinate
                            }
                        }
                    }
                }
            }
            if (coordinate1 != null && coordinate2 !=null && coordinate3 != null) {
                val angle1 = atan2(coordinate1!!.y - coordinate2!!.y, coordinate1!!.x - coordinate2!!.x)
                val angle2 = atan2(coordinate3!!.y - coordinate2!!.y, coordinate3!!.x - coordinate2!!.x)

                var angleDegrees = Math.toDegrees((angle2 - angle1).toDouble()).toFloat()
                if (angleDegrees < 0) {
                    angleDegrees += 360
                }
                return angleDegrees
            }
            return null

        }
        fun calculateAngle(person: Person, firstKeyPos:Int, mainKeyPos:Int, thirdKeyPos:Int): Float {

            val coordinate2=person.keyPoints[mainKeyPos].coordinate
            val coordinate1=person.keyPoints[firstKeyPos].coordinate
            val coordinate3=person.keyPoints[thirdKeyPos].coordinate
            val angle1 = atan2(coordinate1.y - coordinate2.y, coordinate1.x - coordinate2.x)
            val angle2 = atan2(coordinate3.y - coordinate2.y, coordinate3.x - coordinate2.x)

            var angleDegrees = Math.toDegrees((angle2 - angle1).toDouble()).toFloat()
            if (angleDegrees < 0) {
                angleDegrees += 360
            }
            return angleDegrees

        }

        fun angleBetweenPointsForDrawing(center: PointF, point: PointF): Float {
            val deltaX = point.x - center.x
            val deltaY = point.y - center.y
            return Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        }


    }
}