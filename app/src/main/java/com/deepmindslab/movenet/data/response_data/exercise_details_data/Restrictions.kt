package com.deepmindslab.movenet.data.response_data.exercise_details_data

import com.google.gson.annotations.SerializedName


data class Restrictions (

  @SerializedName("AngleArea"                ) var AngleArea                : String?  = null,
  @SerializedName("DrawExtensionFlexion"     ) var DrawExtensionFlexion     : Boolean? = null,
  @SerializedName("NoOfKeyPoints"            ) var NoOfKeyPoints            : Int?     = null,
  @SerializedName("LineType"                 ) var LineType                 : String?  = null,
  @SerializedName("MinValidationValue"       ) var MinValidationValue       : Int?     = null,
  @SerializedName("MaxValidationValue"       ) var MaxValidationValue       : Int?     = null,
  @SerializedName("LowestMinValidationValue" ) var LowestMinValidationValue : Int?     = null,
  @SerializedName("LowestMaxValidationValue" ) var LowestMaxValidationValue : Int?     = null,
  @SerializedName("Scale"                    ) var Scale                    : String?  = null,
  @SerializedName("Direction"                ) var Direction                : String?  = null,
  @SerializedName("StartKeyPosition"         ) var StartKeyPosition         : String?  = null,
  @SerializedName("MiddleKeyPosition"        ) var MiddleKeyPosition        : String?  = null,
  @SerializedName("EndKeyPosition"           ) var EndKeyPosition           : String?  = null

)