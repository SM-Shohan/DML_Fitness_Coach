package com.deepmindslab.movenet

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.deepmindslab.movenet.camera.CameraSource
import com.deepmindslab.movenet.exercise.Exercise
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.ml.ModelType
import com.deepmindslab.movenet.ml.MoveNet
import com.deepmindslab.movenet.ml.MoveNetMultiPose
import com.deepmindslab.movenet.ml.PoseClassifier
import com.deepmindslab.movenet.ml.Type
import com.deepmindslab.movenet.result_data.ExerciseResultData

class ImageProcessingViewModel(application: Application) : ViewModel() {

    private val applicationVm = application
    private var cameraSource: CameraSource? = null
    private var modelPos = 2
    private var isClassifyPose = false



    suspend fun openCamera(exerciseData: ExerciseDataInterface?, exercise: Exercise): LiveData<Bitmap>? {
        val exerciseResultData= ExerciseResultData()

        if (cameraSource == null) {
            cameraSource = CameraSource(applicationVm.applicationContext, object : CameraSource.CameraSourceListener {
                override fun onFPSListener(fps: Int) {
                    //tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                }

                override fun onDetectedInfo(
                    personScore: Float?,
                    poseLabels: List<Pair<String, Float>>?
                ) {
                    poseLabels?.sortedByDescending { it.second }?.let {

                    }
                }

            }).apply {
                prepareCamera()
            }
            isPoseClassifier()
            return cameraSource?.initCamera(exerciseData, exerciseResultData, exercise)

        }
        createPoseEstimator()
        return null
    }


    private fun createPoseEstimator() {

        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                MoveNet.create(applicationVm.applicationContext, ModelType.Lightning)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                MoveNet.create(applicationVm.applicationContext, ModelType.Thunder)
            }
            2 -> {
                MoveNetMultiPose.create(
                    applicationVm.applicationContext,
                    Type.Dynamic
                )
            }
            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(applicationVm.applicationContext) else null)
    }
}