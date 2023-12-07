package com.deepmindslab.movenet.data.response_data.exercise_details_data

import com.google.gson.annotations.SerializedName


data class ExerciseWrapper (

  @SerializedName("ExerciseId" ) var ExerciseId : Int?              = null,
  @SerializedName("Phases"     ) var Phases     : ArrayList<Phases> = arrayListOf()

)