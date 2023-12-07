package com.deepmindslab.movenet.data.response_data.exercise_list_data

import com.google.gson.annotations.SerializedName


data class EvalExerciseProperties (

  @SerializedName("Id"                ) var Id                : Int?    = null,
  @SerializedName("DayNumber"         ) var DayNumber         : Int?    = null,
  @SerializedName("DayName"           ) var DayName           : String? = null,
  @SerializedName("HoldInSeconds"     ) var HoldInSeconds     : Int?    = null,
  @SerializedName("RepetitionInCount" ) var RepetitionInCount : Int?    = null,
  @SerializedName("SetInCount"        ) var SetInCount        : Int?    = null,
  @SerializedName("FrequencyInDay"    ) var FrequencyInDay    : Int?    = null

)