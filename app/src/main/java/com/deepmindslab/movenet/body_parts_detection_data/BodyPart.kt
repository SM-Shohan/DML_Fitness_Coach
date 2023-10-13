package com.deepmindslab.movenet.body_parts_detection_data

enum class BodyPart(val position:Int) {

    NOSE(0),
    RIGHT_EYE(1),
    LEFT_EYE(2),
    RIGHT_EAR(3),
    LEFT_EAR(4),
    RIGHT_SHOULDER(5),
    LEFT_SHOULDER(6),
    RIGHT_ELBOW(7),
    LEFT_ELBOW(8),
    RIGHT_WRIST(9),
    LEFT_WRIST(10),
    RIGHT_HIP(11),
    LEFT_HIP(12),
    RIGHT_KNEE(13),
    LEFT_KNEE(14),
    RIGHT_ANKLE(15),
    LEFT_ANKLE(16);

    companion object{
        private val map = values().associateBy(BodyPart::position)
        fun fromInt(position: Int): BodyPart = map.getValue(position)
    }

}