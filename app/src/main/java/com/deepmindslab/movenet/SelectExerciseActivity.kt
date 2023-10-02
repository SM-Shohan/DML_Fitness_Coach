package com.deepmindslab.movenet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
            val intent = Intent(this,MainActivity::class.java)
            val exerciseName="exercise1"
            intent.putExtra("exerciseName",exerciseName)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val exercise3Object = exercisesArray.getJSONObject(1)
            val exercise3JsonData = exercise3Object.getJSONObject("data")
            val exercise2Data= Exercise2Data(
                exercise3Object.getString("name"),
                exercise3JsonData.getString("minAngleRightElbow").toFloat(),
                exercise3JsonData.getString("maxAngleRightElbow").toFloat(),
                exercise3JsonData.getString("minFreqInMs").toFloat(),
                exercise3JsonData.getString("maxFreqInMs").toFloat()
            )
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("exercise2Data",exercise2Data)
            startActivity(intent)
        }
        button3.setOnClickListener {
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

            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("exercise3Data", exercise3Data)
            startActivity(intent)
        }
    }
}