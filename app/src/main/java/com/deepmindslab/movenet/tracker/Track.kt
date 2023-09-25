package com.deepmindslab.movenet.tracker

import com.deepmindslab.movenet.data.Person

data class Track(
    val person: Person,
    val lastTimestamp: Long
)
