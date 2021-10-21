package com.nomdoa5.nomdo.ui.tasks

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.helpers.adapter.MemberAdapter
import com.nomdoa5.nomdo.databinding.FragmentDetailTaskBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.User
import com.nomdoa5.nomdo.ui.auth.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DetailTaskFragment : Fragment() {
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentDetailTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: Array<String>
    private val memberAdapter = MemberAdapter()
    private var members = arrayListOf<User>()
    private lateinit var rvMember: RecyclerView
    private val args: DetailTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setupViewModel()
        setupRecyclerView()
        setupDetailTask()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData() {
        name = resources.getStringArray(R.array.name)
        for (i in name.indices) {
            val member = User(
                i,
                name[i]
            )
            members.add(member)
        }
    }

    fun setupRecyclerView() {
        rvMember = requireView().findViewById(R.id.rv_member)
        rvMember.setHasFixedSize(true)
        rvMember.addItemDecoration(MemberAdapter.MarginItemDecoration(5))
        val horizontalLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvMember.layoutManager = horizontalLayoutManager
        memberAdapter.setData(members)
        rvMember.adapter = memberAdapter

        memberAdapter.setOnItemClickCallback(object : MemberAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Snackbar.make(
                    requireView(),
                    "Kamu mengklik #${data.name}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        tasksViewModel =
            ViewModelProvider(this).get(TasksViewModel::class.java)
    }

    fun setupDetailTask() {
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            tasksViewModel.setDetailTask(it!!, args.idTask)
        })

        tasksViewModel.getDetailTask().observe(viewLifecycleOwner, {
            binding.tvDescription.setText(it.taskDescription)
            binding.tvCalendar.text = "-"
            binding.tvSpendedMoney.text = "-"
        })
    }
}