package com.nomdoa5.nomdo.ui.task

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.adapter.MemberAdapter
import com.nomdoa5.nomdo.adapter.WorkspaceAdapter
import com.nomdoa5.nomdo.databinding.FragmentDetailTaskBinding
import com.nomdoa5.nomdo.model.User
import com.nomdoa5.nomdo.model.Workspace

class DetailTaskFragment : Fragment() {
    private var _binding: FragmentDetailTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: Array<String>
    private val memberAdapter = MemberAdapter()
    private var members = arrayListOf<User>()
    private lateinit var rvMember: RecyclerView

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
        setupRecyclerView()
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

        Log.d("haoeu", "onseusnoeahuesnto")
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
}