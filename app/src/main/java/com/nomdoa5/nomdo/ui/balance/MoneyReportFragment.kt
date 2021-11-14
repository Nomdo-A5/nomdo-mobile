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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentMoneyReportBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import com.nomdoa5.nomdo.ui.board.UpdateBoardDialogFragment
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MoneyReportFragment : Fragment(), BoardAdapter.OnBoardClickListener {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentMoneyReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceAdapter: BalanceAdapter
    private lateinit var rvBalance: RecyclerView
    private val args: MoneyReportFragmentArgs by navArgs()

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

    private fun getList(): ArrayList<Balance> {
        val listBalance = ArrayList<Balance>()
        listBalance.add(Balance(1, 2000, "Foto Copy", 1))
        listBalance.add(Balance(2, 0, "Paid Promote", 0))
        listBalance.add(Balance(3, 20000, "Konsumsi", 1))
        listBalance.add(
            Balance(
                4,
                30000,
                "Jiahahahahy hayuuu mabar dulu bro eits tunggu dulu bro kita ",
                1
            )
        )
        listBalance.add(Balance(5, 300000, "aoeu", 0))
        listBalance.add(Balance(6, 24000, "jiahahay", 1))


        return listBalance
    }

    fun setupRecyclerView() {
        balanceAdapter = BalanceAdapter(requireContext())
        rvBalance = requireView().findViewById(R.id.rv_balance)
        rvBalance.setHasFixedSize(true)
        rvBalance.layoutManager = LinearLayoutManager(context)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            balanceViewModel.setAllBalance(it!!, args.workspace.id.toString())
        })

        balanceViewModel.getBalance().observe(viewLifecycleOwner, {
            balanceAdapter.setData(it)
        })
        rvBalance.adapter = balanceAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardsViewModel =
            ViewModelProvider(this).get(BoardViewModel::class.java)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
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