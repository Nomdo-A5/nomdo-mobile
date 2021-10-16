package com.nomdoa5.nomdo.ui.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.adapter.TaskAdapter
import com.nomdoa5.nomdo.databinding.FragmentTaskBinding
import com.nomdoa5.nomdo.repository.model.Task

class TaskFragment : Fragment() {
    //    private lateinit var myWorkspacesViewModel: MyWorkspacesViewModel
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val taskAdapter = TaskAdapter()
    private var tasks = arrayListOf<Task>()
    private lateinit var taskName: Array<String>
    private lateinit var createdAt: Array<String>
    private lateinit var rvTask: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        myWorkspacesViewModel =
//            ViewModelProvider(this).get(MyWorkspacesViewModel::class.java)

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        myWorkspacesViewModel.text.observe(viewLifecycleOwner, {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData(){
        taskName = resources.getStringArray(R.array.name)
        createdAt = resources.getStringArray(R.array.creator)

        for(i in taskName.indices){
            val task = Task(
                i,
                taskName[i],
                "Owned by " + createdAt[i],
            )
            tasks.add(task)
        }
    }

    fun setupRecyclerView(){
        rvTask = requireView().findViewById(R.id.rv_tasks)
        rvTask.setHasFixedSize(true)
        rvTask.addItemDecoration(TaskAdapter.MarginItemDecoration(15))
        rvTask.layoutManager = LinearLayoutManager(context)
        taskAdapter.setData(tasks)
        rvTask.adapter = taskAdapter

        taskAdapter.setOnItemClickCallback(object : TaskAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Task) {
                Snackbar.make(requireView(), "Kamu mengklik #${data.idTask}", Snackbar.LENGTH_SHORT).show()
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_tasks_to_detailTaskFragment)
            }
        })
    }
}