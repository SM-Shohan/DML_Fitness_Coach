package com.deepmindslab.movenet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.deepmindslab.movenet.exercise.Exercise1
import com.deepmindslab.movenet.exercise.Exercise2
import com.deepmindslab.movenet.exercise.Exercise3
import com.deepmindslab.movenet.exercise_data.Exercise1Data
import com.deepmindslab.movenet.exercise_data.Exercise2Data
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import org.json.JSONArray
import org.json.JSONObject

class SelectExerciseActivity : AppCompatActivity() {
    private lateinit var exercisesArray:JSONArray


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exercise)

        val button1:Button=findViewById(R.id.exercise_1)
        val button2:Button=findViewById(R.id.exercise_2)
        val button3:Button=findViewById(R.id.exercise_3)

        val assetManager = applicationContext.assets

        try {
            // Open and read the JSON file
            val inputStream = assetManager.open("exercise.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)
            exercisesArray = jsonObject.getJSONArray("exercises")

        } catch (e: Exception) {
            // Handle any exceptions that may occur when accessing the file
        }


        button1.setOnClickListener {
            val exercise1=Exercise1()
            val exercise1Object = exercisesArray.getJSONObject(0)
            val exercise1JsonData = exercise1Object.getJSONObject("data")
            val exercise1Data= Exercise1Data(
                exercise1Object.getString("name"),
                exercise1JsonData.getString("maxAngleShoulderWristUp").toFloat(),
                exercise1JsonData.getString("minAngleShoulderWristUp").toFloat(),
                exercise1JsonData.getString("maxAngleShoulderWristDown").toFloat(),
                exercise1JsonData.getString("minAngleShoulderWristDown").toFloat()
            )
            val intent = Intent(this,ImageProcessingActivity::class.java)
            intent.putExtra("exercise1Data",exercise1Data)
            intent.putExtra("exercise1",exercise1)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val exercise2=Exercise2()
            val exercise2Object = exercisesArray.getJSONObject(1)
            val exercise2JsonData = exercise2Object.getJSONObject("data")
            val exercise2Data= Exercise2Data(
                exercise2Object.getString("name"),
                exercise2JsonData.getString("minAngleRightElbow").toFloat(),
                exercise2JsonData.getString("maxAngleRightElbow").toFloat(),
                exercise2JsonData.getString("minFreqInMs").toFloat(),
                exercise2JsonData.getString("maxFreqInMs").toFloat()
            )
            val intent=Intent(this,ImageProcessingActivity::class.java)
            intent.putExtra("exercise2Data",exercise2Data)
            intent.putExtra("exercise2",exercise2)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val exercise3=Exercise3()
            val exercise3Object = exercisesArray.getJSONObject(2)
            val exercise3JsonData = exercise3Object.getJSONObject("data")

            val exercise3Data=Exercise3Data(
                exercise3Object.getString("name"),
                exercise3JsonData.getString("minRatioOfRUpperAndLower").toFloat(),
                exercise3JsonData.getString("maxRatioOfRUpperAndLower").toFloat(),
                exercise3JsonData.getString("idealRatioOfRUpperAndLower").toFloat(),
                exercise3JsonData.getString("minRatioOfWristsAndShoulders").toFloat(),
                exercise3JsonData.getString("maxRatioOfWristsAndShoulders").toFloat(),
                exercise3JsonData.getString("maxRatioOfBodyWrist").toFloat(),
                exercise3JsonData.getString("minRatioOfElbowAndShoulders").toFloat(),
                exercise3JsonData.getString("maxRatioOfElbowAndShoulders").toFloat()
            )

            val intent=Intent(this,ImageProcessingActivity::class.java)
            intent.putExtra("exercise3Data", exercise3Data)
            intent.putExtra("exercise3",exercise3)
            startActivity(intent)
        }
    }
}