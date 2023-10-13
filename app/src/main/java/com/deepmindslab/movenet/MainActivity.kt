package com.deepmindslab.movenet

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.view.SurfaceView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.deepmindslab.movenet.camera.CameraSource
import com.deepmindslab.movenet.exercise.Exercise
import com.deepmindslab.movenet.exercise.Exercise1
import com.deepmindslab.movenet.exercise.Exercise2
import com.deepmindslab.movenet.exercise.Exercise3
import com.deepmindslab.movenet.exercise_data.Exercise1Data
import com.deepmindslab.movenet.exercise_data.Exercise2Data
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.result_data.ExerciseResultData
import com.deepmindslab.movenet.ml.ModelType
import com.deepmindslab.movenet.ml.MoveNet
import com.deepmindslab.movenet.ml.MoveNetMultiPose
import com.deepmindslab.movenet.ml.PoseClassifier
import com.deepmindslab.movenet.ml.TrackerType
import com.deepmindslab.movenet.ml.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    private lateinit var surfaceView: SurfaceView

    private var modelPos = 2


    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false

    private var exerciseData: ExerciseDataInterface? = null
    private var exercise:Exercise?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView=findViewById(R.id.surfaceView)

        if (intent.hasExtra("exercise1Data")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                exerciseData = intent.getSerializableExtra("exercise1Data", Exercise1Data::class.java) as ExerciseDataInterface
                exercise=intent.getSerializableExtra("exercise1",Exercise1::class.java)
            } else {
                exerciseData = intent.getSerializableExtra("exercise1Data") as ExerciseDataInterface
                exercise=intent.getSerializableExtra("exercise1") as Exercise1
            }

        }else if (intent.hasExtra("exercise2Data")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                exerciseData = intent.getSerializableExtra("exercise2Data",Exercise2Data::class.java) as ExerciseDataInterface
                exercise = intent.getSerializableExtra("exercise2",Exercise2::class.java)
            }else {
                exerciseData = intent.getSerializableExtra("exercise2Data") as ExerciseDataInterface
                exercise = intent.getSerializableExtra("exercise2") as Exercise2
            }
        }else if (intent.hasExtra("exercise3Data")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                exerciseData = intent.getSerializableExtra("exercise3Data",Exercise3Data::class.java) as ExerciseDataInterface
                exercise=intent.getSerializableExtra("exercise3",Exercise3::class.java)
            }else {
                exerciseData = intent.getSerializableExtra("exercise3Data") as ExerciseDataInterface
                exercise = intent.getSerializableExtra("exercise3") as Exercise3
            }
        }

        if (exerciseData!=null && exercise != null){
            openCamera(exerciseData, exercise!!)
        }



        val onBackPressedDispatcher = this.onBackPressedDispatcher
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                cameraSource?.close()
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }


    override fun onDestroy() {
        cameraSource?.close()
        super.onDestroy()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera(exerciseData: ExerciseDataInterface?, exercise: Exercise) {
        val exerciseResultData= ExerciseResultData()

        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource = CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
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
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera(exerciseData, exerciseResultData, exercise,this@MainActivity,
                        cameraSource!!
                    )
                }
            }
            createPoseEstimator()
        }
    }


    private fun createPoseEstimator() {

        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                MoveNet.create(this, ModelType.Lightning)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                MoveNet.create(this, ModelType.Thunder)
            }
            2 -> {
                MoveNetMultiPose.create(
                    this,
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
        cameraSource?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
    }

    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }


}