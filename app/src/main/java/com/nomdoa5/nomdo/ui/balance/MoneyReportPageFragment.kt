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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentMoneyReportPageBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter
import com.nomdoa5.nomdo.helpers.toCurrencyFormat
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.Workspace
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MoneyReportPageFragment(
    private var workspaceArgs: Workspace? = null,
    private var type: Int?
) :
    Fragment(),
    View.OnClickListener,
    BalanceAdapter.OnBalanceClickListener, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentMoneyReportPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceIncomeAdapter: BalanceAdapter
    private lateinit var balanceOutcomeAdapter: BalanceAdapter
    private lateinit var rvIncomeBalance: RecyclerView
    private lateinit var rvOutcomeBalance: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (savedInstanceState != null) {
            workspaceArgs = savedInstanceState.getParcelable("args")
        }
        _binding = FragmentMoneyReportPageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupOverview()
        binding.tvIncomeDetailMoneyReport.setOnClickListener(this)
        binding.tvOutcomeDetailMoneyReport.setOnClickListener(this)
        binding.swipeMyMoneyReport.setOnRefreshListener(this)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            balanceViewModel.balanceState.collect {
                when (it) {
                    is LoadingState.Success -> {
                        binding.swipeMyMoneyReport.isRefreshing = false
                    }
                    is LoadingState.Loading -> {
                        binding.swipeMyMoneyReport.isRefreshing = true
                    }
                    is LoadingState.Error -> {
                        showSnackbar(it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("args", workspaceArgs)
    }

    fun setupRecyclerView() {
        balanceIncomeAdapter = BalanceAdapter(requireContext(), this)
        balanceOutcomeAdapter = BalanceAdapter(requireContext(), this)

        rvIncomeBalance = requireView().findViewById(R.id.rv_income_balance)
        rvOutcomeBalance = requireView().findViewById(R.id.rv_outcome_balance)

        rvIncomeBalance.setHasFixedSize(true)
        rvOutcomeBalance.setHasFixedSize(true)

        rvIncomeBalance.layoutManager = LinearLayoutManager(context)
        rvOutcomeBalance.layoutManager = LinearLayoutManager(context)

        setupDataAdapter()
    }

    private fun setupDataAdapter() {
        val workspaceId = workspaceArgs!!.id.toString()
        when (type) {
            0 -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    with(balanceViewModel) {
                        setBalance(it!!, workspaceId, 1)
                        setBalance(it, workspaceId, 0)
                        setOverviewBalance(it, workspaceId)
                    }
                })

                balanceViewModel.getIncomeBalance().observe(viewLifecycleOwner, {
                    val data = if (it.size > 3) it.reversed().subList(0, 3)
                        .toCollection(ArrayList()) else it.reversed().toCollection(ArrayList())
                    balanceIncomeAdapter.setData(data)
                })

                balanceViewModel.getOutcomeBalance().observe(viewLifecycleOwner, {
                    val data = if (it.size > 3) it.reversed().subList(0, 3)
                        .toCollection(ArrayList()) else it.reversed().toCollection(ArrayList())
                    balanceOutcomeAdapter.setData(data)
                })
            }
            1 -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    with(balanceViewModel) {
                        setBalance(
                            it!!,
                            workspaceId,
                            1,
                            "Planned"
                        )
                        setBalance(it, workspaceId, 0, "Planned")
                        setOverviewBalance(it, workspaceId)
                    }
                })


                balanceViewModel.getIncomePlannedBalance().observe(viewLifecycleOwner, {
                    val data = if (it.size > 3) it.reversed().subList(0, 3)
                        .toCollection(ArrayList()) else it.reversed().toCollection(ArrayList())
                    balanceIncomeAdapter.setData(data)
                })

                balanceViewModel.getOutcomePlannedBalance().observe(viewLifecycleOwner, {
                    val data = if (it.size > 3) it.reversed().subList(0, 3)
                        .toCollection(ArrayList()) else it.reversed().toCollection(ArrayList())
                    balanceOutcomeAdapter.setData(data)
                })
            }
            2 -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    with(balanceViewModel) {
                        setBalance(it!!, workspaceId, 1, "Done")
                        setBalance(it, workspaceId, 0, "Done")
                        setOverviewBalance(it, workspaceId)
                    }
                })

                balanceViewModel.getIncomeDoneBalance().observe(viewLifecycleOwner, {
                    val data = if (it.size > 3) it.reversed().subList(0, 3)
                        .toCollection(ArrayList()) else it.reversed().toCollection(ArrayList())
                    balanceIncomeAdapter.setData(data)
                })

                balanceViewModel.getOutcomeDoneBalance().observe(viewLifecycleOwner, {
                    val data = if (it.size > 3) it.reversed().subList(0, 3)
                        .toCollection(ArrayList()) else it.reversed().toCollection(ArrayList())
                    balanceOutcomeAdapter.setData(data)
                })
            }
        }

        rvIncomeBalance.adapter = balanceIncomeAdapter
        rvOutcomeBalance.adapter = balanceOutcomeAdapter
    }

    private fun setupOverview() {
        when (type) {
            0 -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    balanceViewModel.setOverviewBalance(it!!, workspaceArgs!!.id.toString())
                })

                with(balanceViewModel) {
                    getOverviewBalance().observe(viewLifecycleOwner, {
                        binding.tvNominalIncomeOverviewMoneyReport.text =
                            it.incomeBalance.toCurrencyFormat()
                        binding.tvNominalOutcomeOverviewMoneyReport.text =
                            it.outcomeBalance.toCurrencyFormat()
                        binding.tvNominalBalanceOverviewMoneyReport.text =
                            it.totalBalance.toCurrencyFormat()
                    })
                }
            }
            2 -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    balanceViewModel.setOverviewBalance(
                        it!!,
                        workspaceArgs!!.id.toString(),
                        "Done"
                    )
                })

                with(balanceViewModel) {
                    getOverviewDoneBalance().observe(viewLifecycleOwner, {
                        binding.tvNominalIncomeOverviewMoneyReport.text =
                            it.incomeBalance.toCurrencyFormat()
                        binding.tvNominalOutcomeOverviewMoneyReport.text =
                            it.outcomeBalance.toCurrencyFormat()
                        binding.tvNominalBalanceOverviewMoneyReport.text =
                            it.totalBalance.toCurrencyFormat()
                    })
                }
            }
            1 -> {
                authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                    balanceViewModel.setOverviewBalance(
                        it!!,
                        workspaceArgs!!.id.toString(),
                        "Planned"
                    )
                })

                with(balanceViewModel) {
                    getOverviewPlannedBalance().observe(viewLifecycleOwner, {
                        binding.tvNominalIncomeOverviewMoneyReport.text =
                            it.incomeBalance.toCurrencyFormat()
                        binding.tvNominalOutcomeOverviewMoneyReport.text =
                            it.outcomeBalance.toCurrencyFormat()
                        binding.tvNominalBalanceOverviewMoneyReport.text =
                            it.totalBalance.toCurrencyFormat()
                    })
                }
            }
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        boardsViewModel =
            ViewModelProvider(this)[BoardViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.tvIncomeDetailMoneyReport -> {
                when (type) {
                    0 -> toDetailMoneyReport(1)
                    1 -> toDetailMoneyReport(1, "Planned")
                    2 -> toDetailMoneyReport(1, "Done")
                }
            }
            binding.tvOutcomeDetailMoneyReport -> {
                when (type) {
                    0 -> toDetailMoneyReport(0)
                    1 -> toDetailMoneyReport(0, "Planned")
                    2 -> toDetailMoneyReport(0, "Done")
                }
            }
        }
    }

    private fun toDetailMoneyReport(isIncome: Int, status: String? = null) {
        val action =
            MoneyReportFragmentDirections.actionMoneyReportFragmentToDetailMoneyReportFragment(
                isIncome,
                workspaceArgs!!,
                status
            )
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onBalanceClick(data: Balance) {
        val updateBalanceDialogFragment = UpdateBalanceDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BALANCE", data)
        updateBalanceDialogFragment.arguments = bundle
        updateBalanceDialogFragment.show(requireActivity().supportFragmentManager, "Update Balance")
    }

    override fun onRefresh() {
        setupDataAdapter()
        setupOverview()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}