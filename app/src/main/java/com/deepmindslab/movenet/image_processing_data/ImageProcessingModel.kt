package com.deepmindslab.movenet.image_processing_data

import android.graphics.Bitmap
import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.ml.PoseClassifier
import com.deepmindslab.movenet.ml.PoseDetector
import com.deepmindslab.movenet.data.result_data.ExerciseResultData

class ImageProcessingModel {
    private lateinit var processedImageLiveData:Bitmap

    fun getProcessedImageLiveData(bitmap: Bitmap,
                                  exerciseData: ExerciseDataInterface?,
                                  exerciseResultData: ExerciseResultData,
                                  lock:Any,
                                  classifier: PoseClassifier?,
                                  detector: PoseDetector?,
    ):Bitmap {

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
        //frameProcessedInOneSecondInterval++
        /*if (frameProcessedInOneSecondInterval == 1) {
            // send fps to view
            listener?.onFPSListener(framesPerSecond)
        }*/

        // if the model returns only one item, show that item's score.
        if (persons.isNotEmpty()) {
            //listener?.onDetectedInfo(persons[0].score, classificationResult)
        }

        return processedImageLiveData
    }
}