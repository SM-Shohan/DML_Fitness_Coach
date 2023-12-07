package com.deepmindslab.movenet.repository

import android.util.Log
import com.deepmindslab.movenet.api_service.ExerciseApiService
import com.deepmindslab.movenet.data.request_data.ExerciseDetailsRequestData
import com.deepmindslab.movenet.data.request_data.ExerciseListRequestData
import com.deepmindslab.movenet.data.response_data.exercise_details_data.ExerciseWrapper
import com.deepmindslab.movenet.data.response_data.exercise_list_data.ExerciseListWrapper
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ExerciseListDataRepository {


    private val client:OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.MINUTES)
            .readTimeout(4, TimeUnit.MINUTES)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://gatewayapi.deepmindlabs.ai").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ExerciseApiService by lazy {
        retrofit.create(ExerciseApiService::class.java)
    }


    fun getPatientExerciseList(tenant: String, testId: String): Call<ExerciseListWrapper>{
        try {
            val requestData = ExerciseListRequestData(tenant, testId)
            return apiService.getPatientExerciseList(requestData)
        } catch (e: Exception) {
            // Log the error for debugging purposes
            Log.e("ExerciseListDataRepository", "Error fetching exercises: ${e.message}", e)
            // You might want to handle the error in a way that makes sense for your app
            throw e
        }
    }
    fun getPatientExerciseDetails(tenant: String, testId: String, exerciseId: String): Call<ExerciseWrapper>{
        try {
            val requestData = ExerciseDetailsRequestData(tenant, testId,exerciseId)
            return apiService.getPatientExerciseDetails(requestData)
        } catch (e: Exception) {
            // Log the error for debugging purposes
            Log.e("ExerciseListDataRepository", "Error fetching exercises: ${e.message}", e)
            // You might want to handle the error in a way that makes sense for your app
            throw e
        }
    }

}