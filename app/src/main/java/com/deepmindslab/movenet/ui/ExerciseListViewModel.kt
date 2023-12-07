package com.deepmindslab.movenet.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepmindslab.movenet.data.response_data.exercise_details_data.ExerciseWrapper
import com.deepmindslab.movenet.data.response_data.exercise_list_data.ExerciseListWrapper
import com.deepmindslab.movenet.data.response_data.exercise_list_data.Exercises
import com.deepmindslab.movenet.repository.ExerciseListDataRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExerciseListViewModel(private val repository: ExerciseListDataRepository) : ViewModel() {
    private val _exerciseList = MutableLiveData<List<Exercises>?>()
    private val _exerciseDetails= MutableLiveData<ExerciseWrapper?>()
    val exerciseList: LiveData<List<Exercises>?> get() = _exerciseList
    val exerciseDetails:LiveData<ExerciseWrapper?> get() = _exerciseDetails

    fun fetchDataList(tenant: String, testId: String) {
        viewModelScope.launch {
            try {

                val response = repository.getPatientExerciseList(tenant, testId)
                response.enqueue(object : Callback<ExerciseListWrapper> {
                    override fun onResponse(
                        call: Call<ExerciseListWrapper>,
                        response: Response<ExerciseListWrapper>
                    ) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            if (responseBody.Exercises.isNotEmpty()) {
                                _exerciseList.value = responseBody.Exercises
                            }else{
                                Log.d("ExerciseListActivity", "API Response is empty")
                            }
                        }else{
                            Log.d("ExerciseListActivity", "API Response is null")
                        }
                    }

                    override fun onFailure(call: Call<ExerciseListWrapper>, t: Throwable) {

                    }
                })
            } catch (e: Exception) {
                Log.e("ExerciseListViewModel", "Error fetching exercises: ${e.message}", e)
                // Handle error (e.g., show a toast or log the error)
            }
        }
    }

    fun fetchDataDetails(tenant: String, testId: String, exerciseId:String){
        viewModelScope.launch {
            try {
                val response = repository.getPatientExerciseDetails(tenant, testId, exerciseId)
                response.enqueue(object : Callback<ExerciseWrapper> {
                    override fun onResponse(
                        call: Call<ExerciseWrapper>,
                        response: Response<ExerciseWrapper>
                    ) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            _exerciseDetails.value= responseBody
                        }else{
                            Log.d("ExerciseListActivity", "API Response is null")
                        }
                    }

                    override fun onFailure(call: Call<ExerciseWrapper>, t: Throwable) {

                    }
                })
            } catch (e: Exception) {
                Log.e("ExerciseListViewModel", "Error fetching exercises: ${e.message}", e)
                // Handle error (e.g., show a toast or log the error)
            }
        }
    }


}
