package com.deepmindslab.movenet.ml

import android.graphics.Bitmap
import com.deepmindslab.movenet.body_parts_detection_data.Person

interface PoseDetector : AutoCloseable {

    fun estimatePoses(bitmap: Bitmap): List<Person>

    fun lastInferenceTimeNanos(): Long
}