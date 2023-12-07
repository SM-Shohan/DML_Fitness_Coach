package com.deepmindslab.movenet.data.response_data.exercise_details_data

import com.google.gson.annotations.SerializedName


data class Phases (

  @SerializedName("HoldInSeconds" ) var HoldInSeconds : Int?                    = null,
  @SerializedName("Restrictions"  ) var Restrictions  : ArrayList<Restrictions> = arrayListOf(),
  @SerializedName("PhaseNumber"   ) var PhaseNumber   : Int?                    = null,
  @SerializedName("PhaseDialogue" ) var PhaseDialogue : String?                 = null,
  @SerializedName("CapturedImage" ) var CapturedImage : String?                 = null

)