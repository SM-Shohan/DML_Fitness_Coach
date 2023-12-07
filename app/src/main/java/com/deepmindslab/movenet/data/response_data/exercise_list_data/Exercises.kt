package com.deepmindslab.movenet.data.response_data.exercise_list_data

import com.google.gson.annotations.SerializedName


data class Exercises (

    @SerializedName("ExerciseResponseId"     ) var ExerciseResponseId     : Int?                              = null,
    @SerializedName("ExerciseId"             ) var ExerciseId             : Int?                              = null,
    @SerializedName("ExerciseMedia"          ) var ExerciseMedia          : String?                           = null,
    @SerializedName("ProtocolId"             ) var ProtocolId             : Int?                              = null,
    @SerializedName("ExerciseName"           ) var ExerciseName           : String?                           = null,
    @SerializedName("Instructions"           ) var Instructions           : String?                           = null,
    @SerializedName("ImageURLs"              ) var ImageURLs              : ArrayList<String>                 = arrayListOf(),
    @SerializedName("SetInCount"             ) var SetInCount             : Int?                              = null,
    @SerializedName("RepetitionInCount"      ) var RepetitionInCount      : Int?                              = null,
    @SerializedName("FrequencyInDay"         ) var FrequencyInDay         : Int?                              = null,
    @SerializedName("Phases"                 ) var ExerciseListPhases                 : ArrayList<ExerciseListPhases>                 = arrayListOf(),
    @SerializedName("EvalExerciseProperties" ) var EvalExerciseProperties : ArrayList<EvalExerciseProperties> = arrayListOf()

)