package com.nomdoa5.nomdo.ui.balance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentMoneyReportBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.BalanceModel
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.boards.BoardsViewModel
import com.nomdoa5.nomdo.ui.dialog.UpdateBoardDialogFragment
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MoneyReportFragment : Fragment(), BoardAdapter.OnBoardClickListener {
    private lateinit var boardsViewModel: BoardsViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentMoneyReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceAdapter : BalanceAdapter
    private lateinit var rvBalance: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyReportBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getList(): List<BalanceModel>? {
        val balance_Model_list: MutableList<BalanceModel> = ArrayList()
        balance_Model_list.add(BalanceModel("1", "Foto Copy", "0", "2000"))
        balance_Model_list.add(BalanceModel("2", "Paid Promote", "500000", "0"))
        balance_Model_list.add(BalanceModel("3", "Konsumsi", "0", "20000"))
        return balance_Model_list
    }

    fun setupRecyclerView() {
        balanceAdapter = BalanceAdapter(requireContext(), getList())
        rvBalance = requireView().findViewById(R.id.rv_balance)
        rvBalance.setHasFixedSize(true)
        rvBalance.layoutManager = LinearLayoutManager(context)
        rvBalance.adapter = balanceAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardsViewModel =
            ViewModelProvider(this).get(BoardsViewModel::class.java)
    }

    override fun onBoardClick(data: Board) {
        Snackbar.make(
            requireView(),
            "Kamu mengklik #${data.id}",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onBoardLongClick(data: Board) {
        Toast.makeText(requireContext(), "Longpress ${data.id}", Toast.LENGTH_SHORT).show()
        val addBoardFragment = UpdateBoardDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BOARD", data)
        addBoardFragment.arguments = bundle
        addBoardFragment.show(requireActivity().supportFragmentManager, "Update Dialog")
    }
}