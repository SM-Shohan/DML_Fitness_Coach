package com.deepmindslab.movenet.data.response_data.exercise_list_data

import com.google.gson.annotations.SerializedName


data class ExerciseListPhases (

  @SerializedName("PhaseNumber"   ) var PhaseNumber   : Int?    = null,
  @SerializedName("PhaseDialogue" ) var PhaseDialogue : String? = null,
  @SerializedName("CapturedImage" ) var CapturedImage : String? = null

)