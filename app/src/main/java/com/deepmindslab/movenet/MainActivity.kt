package com.deepmindslab.movenet

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.view.SurfaceView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.deepmindslab.movenet.camera.CameraSource
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import com.deepmindslab.movenet.resultdata.Exercise3ResultData
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView=findViewById(R.id.surfaceView)

        val receivedIntent = intent
        val exerciseData = receivedIntent.getSerializableExtra("exerciseData") as? Exercise3Data
        if (exerciseData!=null){
            openCamera(exerciseData)
        }



        val onBackPressedDispatcher = this.onBackPressedDispatcher
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                cameraSource?.closeCamera()

                this@MainActivity.finish()
            }
        }
        cameraSource?.setTracker(TrackerType.OFF)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }




    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera(exerciseData: Exercise3Data?) {
        val exercise3ResultData= Exercise3ResultData()

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
                    cameraSource?.initCamera(exerciseData, exercise3ResultData,this@MainActivity,
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