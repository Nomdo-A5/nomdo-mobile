package com.nomdoa5.nomdo.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.adapter.HomeAdapter
import com.nomdoa5.nomdo.databinding.FragmentHomeBinding
import com.nomdoa5.nomdo.model.Task

class HomeFragment : Fragment() {
    //    private lateinit var myWorkspacesViewModel: MyWorkspacesViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeAdapter = HomeAdapter()
    private var homes = arrayListOf<Task>()
    private lateinit var homeName: Array<String>
    private lateinit var createdAt: Array<String>
    private lateinit var rvHome: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        myWorkspacesViewModel =
//            ViewModelProvider(this).get(MyWorkspacesViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        homeName = resources.getStringArray(R.array.name)
        createdAt = resources.getStringArray(R.array.creator)

        for(i in homeName.indices){
            val home = Task(
                i,
                homeName[i],
                "Owned by " + createdAt[i],
            )
            homes.add(home)
        }
    }

    fun setupRecyclerView(){
        rvHome = requireView().findViewById(R.id.rv_homes)
        rvHome.setHasFixedSize(true)
        rvHome.addItemDecoration(HomeAdapter.MarginItemDecoration(15))
        rvHome.layoutManager = LinearLayoutManager(context)
        homeAdapter.setData(homes)
        rvHome.adapter = homeAdapter

        homeAdapter.setOnItemClickCallback(object : HomeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Task) {
                Snackbar.make(requireView(), "Kamu mengklik #${data.idTask}", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}