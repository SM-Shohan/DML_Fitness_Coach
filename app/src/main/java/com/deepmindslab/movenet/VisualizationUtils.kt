package com.deepmindslab.movenet

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Typeface
import com.deepmindslab.movenet.accuracy.ExerciseAccuracyUtils
import com.deepmindslab.movenet.angle.AnglesTrackerUtils
import com.deepmindslab.movenet.camera.CameraSource
import com.deepmindslab.movenet.data.BodyPart
import com.deepmindslab.movenet.resultdata.Exercise3ResultData
import com.deepmindslab.movenet.data.Person
import com.deepmindslab.movenet.data.ExerciseData
import com.deepmindslab.movenet.exercise.ExerciseUtils
import com.deepmindslab.movenet.exercise_data.Exercise3Data
import com.deepmindslab.movenet.exercise_validitty.ExerciseValidityUtils
import com.deepmindslab.movenet.posture.PostureUtils
import com.deepmindslab.movenet.standing.StandingUtils
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

    /** Pair of keypoints to draw lines between.  */
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

    private fun drawExerciseDetails(canvas: Canvas, drawLineColorGreen: Boolean, paintText:Paint) {
        val textHeight = paintText.textSize
        val screenWidth = canvas.width
        val screenHeight = canvas.height

        val centerY = (screenHeight + textHeight)/2
            if (drawLineColorGreen) {
                val textWidth = paintText.measureText(ExerciseData.exerciseCounter.toString())
                val centerX = (screenWidth - textWidth) / 2
                canvas.drawText(ExerciseData.exerciseCounter.toString(), centerX, centerY, paintText)
            } else {
                val textWidth = paintText.measureText(ExerciseData.errorMessage)
                val centerX = (screenWidth - textWidth) / 2
                canvas.drawText(ExerciseData.errorMessage, centerX, centerY, paintText)
            }
    }




    private fun drawAngle(centerPoint:PointF, endPoint1:PointF, endPoint2: PointF):Path{
        val path = Path()

        path.moveTo(centerPoint.x, centerPoint.y) // Move to the center point
        path.lineTo(endPoint1.x, endPoint1.y)


        val startAngle=AnglesTrackerUtils.angleBetweenPointsForDrawing(centerPoint, endPoint1)
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
        return path
    }

    // Draw line and point indicate body pose
    fun drawBodyKeyPoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false,
        exerciseData: Exercise3Data?,
        exercise3ResultData: Exercise3ResultData,
    ): Bitmap {



        val paintSector = Paint().apply {
            color = Color.GREEN // Choose your desired color
            style = Paint.Style.FILL
        }


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
        val paintText3 = Paint().apply {
            textSize = 15f
            color = Color.RED
            textAlign = Paint.Align.LEFT
        }


        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)

        val screenWidth = originalSizeCanvas.width
        val screenHeight = originalSizeCanvas.height


        persons.forEach { person ->

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



            val textWidth = paintText2.measureText(ExerciseData.exercise1Counter.toString())
            val textHeight = paintText2.textSize

            val centerX = (screenWidth - textWidth) / 2
            val centerY = (screenHeight + textHeight) / 2
            if (exerciseData?.exerciseName=="exercise1") {
                if (StandingUtils.standingPosition(person)==3){
                    if (ExerciseValidityUtils.exercise1Validity(person)){
                        ExerciseUtils.countExercise1Movement(person)
                        if (ExerciseData.exercise1Counter==0) {
                            originalSizeCanvas.drawText("Start Exercise",
                                centerX,centerY,paintText2)
                        }else {
                            originalSizeCanvas.drawText(
                                ExerciseData.exercise1Counter.toString(),
                                centerX,
                                centerY,
                                paintText2
                            )
                        }
                    }else{
                        originalSizeCanvas.drawText(ExerciseData.errorMessage,centerX, centerY,paintText3)
                    }
                }
                else{
                    originalSizeCanvas.drawText("Stand font facing",
                        centerX,centerY,paintText2)
                }
            }
            else if (exerciseData?.exerciseName=="exercise2")
            {

                val drawLineColorGreen: Boolean


                if (StandingUtils.standingPosition(person)==1){
                    if (ExerciseValidityUtils.exercise2Validity(person)){
                        ExerciseUtils.countExercise2Movement(person)
                        drawLineColorGreen=true
                        if (ExerciseData.exercise2Counter==0){
                            originalSizeCanvas.drawText("Start Exercise",
                                centerX,centerY,paintText2)
                        }else {
                            if (ExerciseData.exercise2Counter>1 && (ExerciseData.exerciseTime>5000L || ExerciseData.exerciseTime<1000L )){
                                originalSizeCanvas.drawText(
                                    "Too slow/fast",
                                    centerX,
                                    centerY,
                                    paintText2
                                )
                                }else{
                                originalSizeCanvas.drawText(
                                    ExerciseData.exercise2Counter.toString(),
                                    centerX,
                                    centerY,
                                    paintText2
                                )
                            }
                            originalSizeCanvas.drawText(
                                ExerciseData.exercise2Counter.toString(),
                                centerX,
                                centerY,
                                paintText2
                            )
                        }
                    }
                    else{
                        drawLineColorGreen=false
                        originalSizeCanvas.drawText(ExerciseData.errorMessage,centerX, centerY,paintText2)
                    }

                }
                else {
                    drawLineColorGreen=false
                    originalSizeCanvas.drawText("Stand left facing",
                        centerX,centerY,paintText2)
                }


                bodyJoints.forEach {
                    val pointA = person.keyPoints[it.first.position].coordinate
                    val pointB = person.keyPoints[it.second.position].coordinate
                    if (person.keyPoints[it.first.position].score>0.25 && person.keyPoints[it.second.position].score>0.25)
                    {
                        if (/*((it.first.position==5 && it.second.position==7) || (it.first.position==7 && it.second.position==9))&& */ drawLineColorGreen ){
                            originalSizeCanvas.drawLine(
                                pointA.x,
                                pointA.y,
                                pointB.x,
                                pointB.y,
                                paintLineGreen
                            )
                        }else {
                            originalSizeCanvas.drawLine(
                                pointA.x,
                                pointA.y,
                                pointB.x,
                                pointB.y,
                                paintLine
                            )

                        }
                    }

                }
                if (drawLineColorGreen) {
                    val path = drawAngle(
                        person.keyPoints[7].coordinate,
                        person.keyPoints[9].coordinate,
                        person.keyPoints[5].coordinate
                    )
                    originalSizeCanvas.drawPath(path, paintSector)
                }


                person.keyPoints.forEach { point ->
                    if (point.score>0.25) {
                        if (/*(point.bodyPart.position==5 || point.bodyPart.position==7 || point.bodyPart.position==9) && */ drawLineColorGreen){
                            originalSizeCanvas.drawCircle(
                                point.coordinate.x,
                                point.coordinate.y,
                                CIRCLE_RADIUS,
                                paintCircleGreen
                            )
                        }else{
                            originalSizeCanvas.drawCircle(
                                point.coordinate.x,
                                point.coordinate.y,
                                CIRCLE_RADIUS,
                                paintCircle
                            )
                        }

                    }

                }


            }

            else if (exerciseData?.exerciseName=="Exercise 3")
            {
                val drawLineColorGreen: Boolean
                if (StandingUtils.standingPosition(person)==3){
                    if (PostureUtils.postureExercise3(person)){
                        if (ExerciseValidityUtils.exercise3Validity(person)){
                            ExerciseUtils.trackExercise3(person,exercise3ResultData)
                            drawLineColorGreen=true
                            drawExerciseDetails(originalSizeCanvas, true,paintText2)
                            drawAccuracy(originalSizeCanvas, ExerciseAccuracyUtils.calculateAccuracy(person,exercise3ResultData),paintText2)

                        }
                        else{
                            drawExerciseDetails(originalSizeCanvas, false,paintText2)
                            drawLineColorGreen=false
                        }
                    }
                    else{
                        drawExerciseDetails(originalSizeCanvas, false,paintText2)
                        drawLineColorGreen=false
                    }
                }else{
                    ExerciseData.errorMessage="Stand Front facing"
                    drawExerciseDetails(originalSizeCanvas, false,paintText2)
                    drawLineColorGreen=false
                }
                drawKeyPoints(originalSizeCanvas,person,drawLineColorGreen)
                drawLine(originalSizeCanvas,person,drawLineColorGreen)
            }


        }
        return output
    }
}