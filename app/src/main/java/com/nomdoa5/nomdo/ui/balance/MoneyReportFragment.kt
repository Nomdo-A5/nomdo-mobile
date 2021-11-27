package com.nomdoa5.nomdo.ui.balance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentMoneyReportBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.board.BoardViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MoneyReportFragment : Fragment(), View.OnClickListener {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentMoneyReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceIncomeAdapter: BalanceAdapter
    private lateinit var balanceOutcomeAdapter: BalanceAdapter
    private lateinit var rvIncomeBalance: RecyclerView
    private lateinit var rvOutcomeBalance: RecyclerView
    private val args: MoneyReportFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyReportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        binding.tvIncomeDetailMoneyReport.setOnClickListener(this)
        binding.tvOutcomeDetailMoneyReport.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupRecyclerView() {
        balanceIncomeAdapter = BalanceAdapter(requireContext())
        balanceOutcomeAdapter = BalanceAdapter(requireContext())

        rvIncomeBalance = requireView().findViewById(R.id.rv_income_balance)
        rvOutcomeBalance = requireView().findViewById(R.id.rv_outcome_balance)


        rvIncomeBalance.setHasFixedSize(true)
        rvOutcomeBalance.setHasFixedSize(true)

        rvIncomeBalance.layoutManager = LinearLayoutManager(context)
        rvOutcomeBalance.layoutManager = LinearLayoutManager(context)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            balanceViewModel.setIncomeBalance(it!!, args.workspace.id.toString())
            balanceViewModel.setOutcomeBalance(it, args.workspace.id.toString())
        })

        balanceViewModel.getIncomeBalance().observe(viewLifecycleOwner, {
            balanceIncomeAdapter.setData(it)
        })

        balanceViewModel.getOutcomeBalance().observe(viewLifecycleOwner, {
            balanceOutcomeAdapter.setData(it)
        })

        rvIncomeBalance.adapter = balanceIncomeAdapter
        rvOutcomeBalance.adapter = balanceOutcomeAdapter
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        boardsViewModel =
            ViewModelProvider(this).get(BoardViewModel::class.java)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvIncomeDetailMoneyReport -> {
                toDetailMoneyReport(true)

            }
            binding.tvOutcomeDetailMoneyReport -> {
                toDetailMoneyReport(false)
            }
        }
    }

    fun toDetailMoneyReport(isIncome: Boolean) {
        val action =
            MoneyReportFragmentDirections.actionMoneyReportFragmentToDetailMoneyReportFragment(
                isIncome,
                args.workspace
            )
        Navigation.findNavController(requireView()).navigate(action)
    }
}