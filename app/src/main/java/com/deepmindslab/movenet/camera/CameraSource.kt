package com.deepmindslab.movenet.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.lifecycle.LiveData
import com.deepmindslab.movenet.VisualizationUtils
import com.deepmindslab.movenet.YuvToRgbConverter
import com.deepmindslab.movenet.result_data.ExerciseResultData
import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise.Exercise
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.ml.MoveNetMultiPose
import com.deepmindslab.movenet.ml.PoseClassifier
import com.deepmindslab.movenet.ml.PoseDetector
import com.deepmindslab.movenet.ml.TrackerType
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraSource(
    context: Context,
    private val listener: CameraSourceListener? = null
) {

    companion object {
        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480

        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .2f
    }

    private val lock = Any()
    private var detector: PoseDetector? = null
    private var classifier: PoseClassifier? = null
    private var isTrackerEnabled = false
    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(context)
    private lateinit var imageBitmap: Bitmap

    private var fpsTimer: Timer? = null
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** Readers used as buffers for camera still shots */
    private var imageReader: ImageReader? = null

    /** The [CameraDevice] that will be opened in this fragment */
    private var camera: CameraDevice? = null

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private var session: CameraCaptureSession? = null

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null
    private var cameraId: String = ""

    suspend fun initCamera(
        exerciseData: ExerciseDataInterface?,
        exerciseResultData: ExerciseResultData,
        exercise: Exercise,
    ): LiveData<Bitmap>? {
        var bitmapLiveData:LiveData<Bitmap>?=null
        //camera = openCamera(cameraManager, cameraId)
        camera= openFrontCamera(cameraManager)

        imageReader =
            ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 3)

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                if (!::imageBitmap.isInitialized) {
                    imageBitmap =
                        Bitmap.createBitmap(
                            PREVIEW_WIDTH,
                            PREVIEW_HEIGHT,
                            Bitmap.Config.ARGB_8888
                        )
                }
                yuvConverter.yuvToRgb(image, imageBitmap)
                // Create rotated version for portrait display
                val rotateMatrix = Matrix()
                rotateMatrix.postRotate(-90.0f)
                rotateMatrix.postScale(-1.0f, 1.0f)

                val rotatedBitmap = Bitmap.createBitmap(
                    imageBitmap, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                    rotateMatrix, false
                )
                bitmapLiveData=processImage(rotatedBitmap,exerciseData,exerciseResultData,exercise)
                image.close()
            }
        }, imageReaderHandler)

        imageReader?.surface?.let { surface ->
            session = createSession(listOf(surface))
            val cameraRequest = camera?.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )?.apply {
                addTarget(surface)
            }
            cameraRequest?.build()?.let {
                session?.setRepeatingRequest(it, null, null)
            }
        }
        return bitmapLiveData
    }

    private suspend fun createSession(targets: List<Surface>): CameraCaptureSession =
        suspendCancellableCoroutine { cont ->
            camera?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(captureSession: CameraCaptureSession) =
                    cont.resume(captureSession)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    cont.resumeWithException(Exception("Session error"))
                }
            }, null)
        }



    private var cameraDevice: CameraDevice? = null
    @SuppressLint("MissingPermission")
    private suspend fun openFrontCamera(manager: CameraManager): CameraDevice? =
        suspendCancellableCoroutine { cont ->
            val cameraIdList = manager.cameraIdList
            var frontCameraId: String? = null

            for (cameraId in cameraIdList) {
                val cameraCharacteristics = manager.getCameraCharacteristics(cameraId)
                val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    // Found the front camera
                    frontCameraId = cameraId
                    break
                }
            }

            if (frontCameraId != null) {
                manager.openCamera(frontCameraId, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        cont.resume(camera)
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        camera.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        if (cont.isActive) cont.resumeWithException(Exception("Camera error"))
                    }
                }, imageReaderHandler)
            } else {
                // Handle the case where there is no front camera
                cont.resumeWithException(Exception("Front camera not found"))
            }
        }


    @SuppressLint("MissingPermission")
    private suspend fun openCamera(manager: CameraManager, cameraId: String): CameraDevice =
        suspendCancellableCoroutine { cont ->
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera // Store the reference
                    cont.resume(camera)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    if (cont.isActive) cont.resumeWithException(Exception("Camera error"))
                }
            }, imageReaderHandler)
        }


    fun resume() {
        imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
        imageReaderHandler = Handler(imageReaderThread!!.looper)
        fpsTimer = Timer()
        fpsTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    fun close() {
        cameraDevice?.close() // Close the camera if it was opened
        cameraDevice = null
        session?.close()
        session = null
        camera?.close()
        camera = null
        imageReader?.close()
        imageReader = null
        stopImageReaderThread()
        detector?.close()
        detector = null
        classifier?.close()
        classifier = null
        fpsTimer?.cancel()
        fpsTimer = null
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
    }


    fun prepareCamera() {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // We don't use a front facing camera in this sample.
            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cameraDirection != null &&
                cameraDirection == CameraCharacteristics.LENS_FACING_FRONT
            ) {
                continue
            }
            this.cameraId = cameraId
        }
    }

    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    fun setClassifier(classifier: PoseClassifier?) {
        synchronized(lock) {
            if (this.classifier != null) {
                this.classifier?.close()
                this.classifier = null
            }
            this.classifier = classifier
        }
    }


    //Set Tracker for Movenet MuiltiPose model.

    fun setTracker(trackerType: TrackerType) {
        isTrackerEnabled = trackerType != TrackerType.OFF
        (this.detector as? MoveNetMultiPose)?.setTracker(trackerType)
    }


    // process image
    private fun processImage(
        bitmap: Bitmap,
        exerciseData: ExerciseDataInterface?,
        exerciseResultData: ExerciseResultData,
        exercise: Exercise,
    ) : LiveData<Bitmap>{
        val persons = mutableListOf<Person>()
        var classificationResult: List<Pair<String, Float>>? = null

        synchronized(lock) {
            detector?.estimatePoses(bitmap)?.let {
                persons.addAll(it)

                // if the model only returns one item, allow running the Pose classifier.

                if (persons.isNotEmpty()) {
                    classifier?.run {
                        classificationResult = classify(persons[0])
                    }
                }
            }
        }
        frameProcessedInOneSecondInterval++
        if (frameProcessedInOneSecondInterval == 1) {
            // send fps to view
            listener?.onFPSListener(framesPerSecond)
        }

        // if the model returns only one item, show that item's score.
        if (persons.isNotEmpty()) {
            listener?.onDetectedInfo(persons[0].score, classificationResult)
        }

        return visualize(persons, bitmap, exerciseData,exerciseResultData,exercise)
    }

    private fun visualize(
        persons: List<Person>,
        bitmap: Bitmap,
        exerciseData: ExerciseDataInterface?,
        exerciseResultData: ExerciseResultData,
        exercise: Exercise,
    ): LiveData<Bitmap> {
        /*if (exerciseResultData.numberOfIteration>=5) {
            cameraSource.close()
            val intent = Intent(mainActivity, ExerciseResultActivity::class.java)
            intent.putExtra("exercise3ResultData", exerciseResultData)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mainActivity.startActivity(intent)
        }*/

        return VisualizationUtils.drawBodyKeyPoints(
            bitmap,
            persons.filter { it.score > MIN_CONFIDENCE },
            isTrackerEnabled,
            exerciseData,
            exerciseResultData,
            exercise
        )

        /*val holder = surfaceView.holder
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int

            if (canvas.height > canvas.width) {
                val ratio = bitmapLiveData.value!!.height.toFloat() / bitmapLiveData.value!!.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = bitmapLiveData.value!!.width.toFloat() / bitmapLiveData.value!!.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                bitmapLiveData.value!!, Rect(0, 0, bitmapLiveData.value!!.width, bitmapLiveData.value!!.height),
                Rect(left, top, right, bottom), null
            )
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }*/
    }

    private fun stopImageReaderThread() {
        imageReaderThread?.quitSafely()
        try {
            imageReaderThread?.join()
            imageReaderThread = null
            imageReaderHandler = null
        } catch (_: InterruptedException) {

        }
    }


    interface CameraSourceListener {
        fun onFPSListener(fps: Int)

        fun onDetectedInfo(personScore: Float?, poseLabels: List<Pair<String, Float>>?)
    }
}