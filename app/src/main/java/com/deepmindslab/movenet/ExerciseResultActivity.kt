package com.deepmindslab.movenet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.deepmindslab.movenet.resultdata.Exercise3ResultData

class ExerciseResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_result)

        val textViewResult=findViewById<TextView>(R.id.activity_exercise_result_number_of_exercise)
        val textViewAccuracy=findViewById<TextView>(R.id.activity_exercise_result_accuracy)

        val receivedIntent = intent
        val exerciseData = receivedIntent.getSerializableExtra("exercise3ResultData") as? Exercise3ResultData
        if (exerciseData != null) {
            // Use the exerciseData object here
            val maxAccuracy = exerciseData.maxAccuracy
            val numberOfExercise = exerciseData.numberOfExercise

            textViewResult.text= String.format(getString(R.string.exercise_max_accuracy),maxAccuracy )
            textViewAccuracy.text= String.format(getString(R.string.number_of_iteration),numberOfExercise )


            // Do something with the values
        } else {
            // Handle the case where the object could not be retrieved properly
        }

    }
}