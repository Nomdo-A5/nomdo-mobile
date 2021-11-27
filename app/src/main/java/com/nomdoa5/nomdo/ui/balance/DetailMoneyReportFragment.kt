package com.nomdoa5.nomdo.ui.balance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.nomdoa5.nomdo.databinding.FragmentDetailMoneyReportBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter
import com.nomdoa5.nomdo.helpers.adapter.BoardAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.Board
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import com.nomdoa5.nomdo.ui.board.UpdateBoardDialogFragment
import com.nomdoa5.nomdo.ui.task.TaskFragmentArgs
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DetailMoneyReportFragment : Fragment() {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentDetailMoneyReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceAdapter: BalanceAdapter
    private lateinit var rvBalance: RecyclerView
    private val args: DetailMoneyReportFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMoneyReportBinding.inflate(inflater, container, false)

        return binding.root
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

    fun setupRecyclerView() {
        balanceAdapter = BalanceAdapter(requireContext())
        rvBalance = requireView().findViewById(R.id.rv_type_detail_balance)
        rvBalance.setHasFixedSize(true)
        rvBalance.layoutManager = LinearLayoutManager(context)

        if (args.isIncome) {
            binding.tvTypeDetailMoneyReport.text = "Income"
            binding.tvTypeDetailMoneyReport.setCompoundDrawablesRelative(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_income),
                null,
                null,
                null
            )
            authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                balanceViewModel.setIncomeBalance(it!!, args.workspace.id.toString())
            })
            balanceViewModel.getIncomeBalance().observe(viewLifecycleOwner, {
                balanceAdapter.setData(it)
            })
        } else {
            binding.tvTypeDetailMoneyReport.text = "Outcome"
            binding.tvTypeDetailMoneyReport.setCompoundDrawablesRelative(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_outcome),
                null,
                null,
                null
            )
            authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                balanceViewModel.setOutcomeBalance(it!!, args.workspace.id.toString())
            })
            balanceViewModel.getOutcomeBalance().observe(viewLifecycleOwner, {
                balanceAdapter.setData(it)
            })
        }



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
}