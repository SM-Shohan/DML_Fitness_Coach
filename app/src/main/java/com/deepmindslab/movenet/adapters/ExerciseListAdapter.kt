package com.deepmindslab.movenet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deepmindslab.movenet.R
import com.deepmindslab.movenet.data.response_data.exercise_list_data.Exercises
import com.squareup.picasso.Picasso

class ExerciseListAdapter:RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    private var exerciseList:List<Exercises> = emptyList()
    private var onImageViewClickListener: OnImageViewClickListener? = null

    fun setData(newExerciseList:List<Exercises>)
    {
        exerciseList=newExerciseList
        notifyItemRangeInserted(0,exerciseList.size)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem=exerciseList[position]

        holder.exerciseTitle.text=currentItem.ExerciseName
        holder.exerciseSet.text=currentItem.ExerciseId.toString()
        Picasso.get().load(currentItem.ImageURLs[0]).into(holder.imageView)



        holder.imageView.setOnClickListener {
            // Trigger the listener when the imageView is clicked
            onImageViewClickListener?.onImageViewClick(position)
        }
    }

    interface OnImageViewClickListener {
        fun onImageViewClick(position: Int)
    }

    fun setOnImageViewClickListener(listener: OnImageViewClickListener) {
        this.onImageViewClickListener = listener
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseTitle:TextView=itemView.findViewById(R.id.exercise_item_list_exercise_name)
        val exerciseSet:TextView=itemView.findViewById(R.id.exercise_item_list_exercise_set)
        val imageView:ImageView=itemView.findViewById(R.id.circularImageView)
    }
}