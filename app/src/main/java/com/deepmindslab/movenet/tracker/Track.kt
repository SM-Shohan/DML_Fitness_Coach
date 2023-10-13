package com.deepmindslab.movenet.tracker

import com.deepmindslab.movenet.body_parts_detection_data.Person

data class Track(
    val person: Person,
    val lastTimestamp: Long
)
