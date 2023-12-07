package com.deepmindslab.movenet

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Typeface
import com.deepmindslab.movenet.body_parts_detection_data.BodyPart
import com.deepmindslab.movenet.body_parts_detection_data.Person
import com.deepmindslab.movenet.exercise.Exercise
import com.deepmindslab.movenet.exercise_data.Exercise1Data
import com.deepmindslab.movenet.exercise_data.Exercise2Data
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import com.deepmindslab.movenet.exercise_data.ExerciseDataInterface
import com.deepmindslab.movenet.exercise_utility_classes.AnglesTrackerUtils
import com.deepmindslab.movenet.data.result_data.ExerciseResultData
import kotlin.math.max

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 6f
    private const val CIRCLE_RADIUS_2 = 50
    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 4f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    /** Pair of key points to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )



    private fun drawAccuracy(canvas: Canvas, accuracy: Float,paintText: Paint) {
        canvas.drawText(accuracy.toString(),
            25f,25f,paintText)
    }

    private fun drawKeyPoints(canvas: Canvas, person: Person, drawLineColorGreen: Boolean) {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL
        }
        val paintCircleGreen = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.GREEN
            style = Paint.Style.FILL
        }

        person.keyPoints.forEach { point ->
            if (drawLineColorGreen){
                canvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircleGreen
                )
            }else{
                canvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }

        }
    }

    private  fun  drawLine(canvas: Canvas, person:Person, drawLineColorGreen: Boolean){
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }

        val paintLineGreen = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.GREEN
            style = Paint.Style.STROKE
        }
        bodyJoints.forEach {
            val pointA = person.keyPoints[it.first.position].coordinate
            val pointB = person.keyPoints[it.second.position].coordinate
            if(drawLineColorGreen){
                canvas.drawLine(
                    pointA.x,
                    pointA.y,
                    pointB.x,
                    pointB.y,
                    paintLineGreen)
            }else{
                canvas.drawLine(
                    pointA.x,
                    pointA.y,
                    pointB.x,
                    pointB.y,
                    paintLine)
            }

        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun drawExerciseDetails(
        canvas: Canvas,
        exercise3ResultData: ExerciseResultData,
        drawLineColorGreen: Boolean,
        paintText: Paint
    ) {
        val screenWidth = canvas.width
        val screenHeight = canvas.height
        val textHeight = paintText.textSize
        val centerY = (screenHeight + textHeight)/2
            if (drawLineColorGreen) {
                val textWidth = paintText.measureText(exercise3ResultData.numberOfIteration.toString())
                val centerX = (screenWidth - textWidth) / 2
                canvas.drawText(exercise3ResultData.numberOfIteration.toString(), centerX, centerY, paintText)
            } else {
                val textWidth = paintText.measureText(exercise3ResultData.exerciseErrorMessage)
                val centerX = (screenWidth - textWidth) / 2
                canvas.drawText(exercise3ResultData.exerciseErrorMessage, centerX, centerY, paintText)
            }
    }




    private fun drawAngle(canvas: Canvas,centerPoint:PointF, endPoint1:PointF, endPoint2: PointF,paintSector:Paint){
        val path = Path()

        path.moveTo(centerPoint.x, centerPoint.y) // Move to the center point
        path.lineTo(endPoint1.x, endPoint1.y)


        val startAngle= AnglesTrackerUtils.angleBetweenPointsForDrawing(centerPoint, endPoint1)
        val endAngle= AnglesTrackerUtils.angleBetweenPointsForDrawing(centerPoint, endPoint2)

        val sweepAngle = if (endAngle >= startAngle) {
            endAngle - startAngle
        } else {
            360f - startAngle + endAngle
        }

        path.arcTo(
            RectF(centerPoint.x - CIRCLE_RADIUS_2, centerPoint.y - CIRCLE_RADIUS_2,
                centerPoint.x + CIRCLE_RADIUS_2, centerPoint.y + CIRCLE_RADIUS_2),
            startAngle,
            sweepAngle
        )
        path.close()

        canvas.drawPath(path, paintSector)
    }


    // Draw line and point indicate body pose
    fun drawBodyKeyPoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false,
        exerciseData: ExerciseDataInterface?,
        exerciseResultData: ExerciseResultData,
        exercise: Exercise,
    ): Bitmap {

        val sortPersons = persons.sortedBy { it.keyPoints.firstOrNull()?.coordinate?.x ?: 0f }

        val paintSector = Paint().apply {
            color = Color.GREEN // Choose your desired color
            style = Paint.Style.FILL
        }

        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }

        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.BLUE
            textAlign = Paint.Align.LEFT
        }
        val paintText2 = Paint().apply {
            textSize = 30f
            color = Color.RED
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)


        sortPersons.forEach { person ->
            if (person == sortPersons[0]) {
                // draw person id if tracker is enable
                if (isTrackerEnabled) {
                    person.boundingBox?.let {
                        val personIdX = max(0f, it.left)
                        val personIdY = max(0f, it.top)

                        originalSizeCanvas.drawText(
                            person.id.toString(),
                            personIdX,
                            personIdY - PERSON_ID_MARGIN,
                            paintText
                        )
                        originalSizeCanvas.drawRect(it, paintLine)
                    }
                }

                when (exerciseData) {
                    is Exercise1Data -> {
                        val drawLineColorGreen: Boolean
                        if (exercise.exerciseValidity(person,exerciseData,exerciseResultData)) {
                            drawLineColorGreen=true
                            exercise.numberOfIteration(person,exerciseData,exerciseResultData)
                            drawExerciseDetails(originalSizeCanvas,exerciseResultData,true,paintText2)
                        } else {
                            drawLineColorGreen=false
                            drawExerciseDetails(originalSizeCanvas,exerciseResultData,false,paintText2)
                        }
                        drawLine(originalSizeCanvas, person, drawLineColorGreen)
                        drawKeyPoints(originalSizeCanvas, person, drawLineColorGreen)
                    }

                    is Exercise2Data -> {
                        var drawLineColorGreen: Boolean
                        if (exercise.exerciseValidity(person,exerciseData,exerciseResultData)) {
                            exercise.numberOfIteration(person,exerciseData,exerciseResultData)
                            drawLineColorGreen = true
                            if (exerciseResultData.numberOfIteration > 1 && (exerciseResultData.timePerIteration > 5000L || exerciseResultData.timePerIteration < 1000L)) {
                                exerciseResultData.exerciseErrorMessage = "Too slow or fast"
                                drawLineColorGreen = false
                            }
                        } else {
                            drawLineColorGreen = false
                        }
                        drawExerciseDetails(
                            originalSizeCanvas,
                            exerciseResultData,
                            drawLineColorGreen,
                            paintText2
                        )
                        drawLine(originalSizeCanvas, person, drawLineColorGreen)
                        if (drawLineColorGreen) {
                            drawAngle(originalSizeCanvas,
                                person.keyPoints[7].coordinate,
                                person.keyPoints[9].coordinate,
                                person.keyPoints[5].coordinate,paintSector
                            )
                        }
                        drawKeyPoints(originalSizeCanvas, person, drawLineColorGreen)

                    }

                    is Exercise3Data -> {
                        val drawLineColorGreen: Boolean
                        if (exercise.exerciseValidity(person,exerciseData,exerciseResultData)) {
                            exercise.numberOfIteration(
                                person,
                                exerciseData,
                                exerciseResultData
                            )
                            exercise.calculateCurrentAccuracy(
                                person,exerciseData,
                                exerciseResultData
                            )
                            exercise.calculateMaxAccuracy(person,exerciseData,
                                exerciseResultData)
                            drawLineColorGreen = true
                            drawExerciseDetails(originalSizeCanvas, exerciseResultData,true, paintText2)
                            drawAccuracy(
                                originalSizeCanvas, exerciseResultData.currentAccuracy, paintText2
                            )

                        } else {
                            drawExerciseDetails(
                                originalSizeCanvas,
                                exerciseResultData,
                                false,
                                paintText2
                            )
                            drawLineColorGreen = false
                        }
                        drawKeyPoints(originalSizeCanvas, person, drawLineColorGreen)
                        drawLine(originalSizeCanvas, person, drawLineColorGreen)
                    }
                }


            }else{
                drawKeyPoints(originalSizeCanvas,person,false)
                drawLine(originalSizeCanvas,person,false)
            }
        }
        return output
    }
}