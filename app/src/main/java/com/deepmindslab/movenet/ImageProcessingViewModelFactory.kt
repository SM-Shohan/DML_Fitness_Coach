package com.deepmindslab.movenet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageProcessingViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageProcessingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageProcessingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
