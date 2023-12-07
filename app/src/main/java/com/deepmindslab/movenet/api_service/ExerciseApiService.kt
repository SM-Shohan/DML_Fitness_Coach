package com.deepmindslab.movenet.api_service

import com.deepmindslab.movenet.data.request_data.ExerciseDetailsRequestData
import com.deepmindslab.movenet.data.request_data.ExerciseListRequestData
import com.deepmindslab.movenet.data.response_data.exercise_details_data.ExerciseWrapper
import com.deepmindslab.movenet.data.response_data.exercise_list_data.ExerciseListWrapper
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ExerciseApiService {
    @POST("/api/exercisekeypoint/GetPatientExercises")
    fun getPatientExerciseList(@Body requestData: ExerciseListRequestData): Call<ExerciseListWrapper>

    @POST("/api/exercisekeypoint/GetPatientExerciseRestrictions")
    fun getPatientExerciseDetails(@Body requestData: ExerciseDetailsRequestData): Call<ExerciseWrapper>
}