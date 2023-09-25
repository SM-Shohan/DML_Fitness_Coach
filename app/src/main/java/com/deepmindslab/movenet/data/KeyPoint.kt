package com.deepmindslab.movenet.data

import android.graphics.PointF

data class KeyPoint(val  bodyPart: BodyPart, var coordinate:PointF, val score:Float)
