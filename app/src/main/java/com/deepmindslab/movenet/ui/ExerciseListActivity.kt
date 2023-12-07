package com.deepmindslab.movenet.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deepmindslab.movenet.R
import com.deepmindslab.movenet.adapters.ExerciseListAdapter
import com.deepmindslab.movenet.repository.ExerciseListDataRepository
import com.deepmindslab.movenet.utlities.LoadingDialogFragment

class ExerciseListActivity : AppCompatActivity(),ExerciseListAdapter.OnImageViewClickListener {

    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter: ExerciseListAdapter
    private val viewModel: ExerciseListViewModel by viewModels { ExerciseListViewModelFactory(ExerciseListDataRepository()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)


        val loadingDialog = LoadingDialogFragment.newInstance()
        loadingDialog.show(supportFragmentManager,"loading dialog")

        recyclerView=findViewById(R.id.activity_exercise_list_recycler_view)
        recyclerView.layoutManager=LinearLayoutManager(this)

        adapter=ExerciseListAdapter()
        recyclerView.adapter=adapter
        adapter.setOnImageViewClickListener(this)


        viewModel.exerciseList.observe(this) { itemList ->
            loadingDialog.dismiss()
            if (itemList != null) {
                adapter.setData(itemList)
            }
        }
        viewModel.exerciseDetails.observe(this){
            if (it!=null){
                //Toast.makeText(this,it.ExerciseId.toString(),Toast.LENGTH_SHORT).show()

            }
        }
        viewModel.fetchDataList("admin","TEST-17998")
    }

    override fun onImageViewClick(position: Int) {
        viewModel.fetchDataDetails("admin","TEST-17998","75")
    }

}