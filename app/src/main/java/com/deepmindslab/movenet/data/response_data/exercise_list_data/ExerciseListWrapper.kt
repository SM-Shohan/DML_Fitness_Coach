package com.deepmindslab.movenet.data.response_data.exercise_list_data

import com.google.gson.annotations.SerializedName


data class ExerciseListWrapper (

  @SerializedName("Exercises" ) var Exercises : ArrayList<Exercises> = arrayListOf()

)