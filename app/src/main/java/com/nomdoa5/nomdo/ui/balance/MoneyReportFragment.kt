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
import com.nomdoa5.nomdo.helpers.toCurrencyFormat
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.board.BoardViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MoneyReportFragment : Fragment(), View.OnClickListener,
    BalanceAdapter.OnBalanceClickListener {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var boardsViewModel: BoardViewModel
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentMoneyReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceIncomeAdapter: BalanceAdapter
    private lateinit var balanceOutcomeAdapter: BalanceAdapter
    private lateinit var rvIncomeBalance: RecyclerView
    private lateinit var rvOutcomeBalance: RecyclerView
    private var isAllSet: Boolean = false
    private var isPlannedSet: Boolean = false
    private var isDoneSet: Boolean = false
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
        setupOverview()
        binding.tvIncomeDetailMoneyReport.setOnClickListener(this)
        binding.tvOutcomeDetailMoneyReport.setOnClickListener(this)
        binding.btnAllMoneyReport.setOnClickListener(this)
        binding.btnDoneMoneyReport.setOnClickListener(this)
        binding.btnPlannedMoneyReport.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        when {
            binding.btnAllMoneyReport.isChecked -> {
                if (!isAllSet) {
                    authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                        balanceViewModel.setBalance(it!!, args.workspace.id.toString(), 1)
                        balanceViewModel.setBalance(it, args.workspace.id.toString(), 0)
                        balanceViewModel.setOverviewBalance(it, args.workspace.id.toString())
                    })
                }
                balanceViewModel.getIncomeBalance().observe(viewLifecycleOwner, {
                    balanceIncomeAdapter.setData(it)
                })

                balanceViewModel.getOutcomeBalance().observe(viewLifecycleOwner, {
                    balanceOutcomeAdapter.setData(it)
                })
            }
            binding.btnDoneMoneyReport.isChecked -> {
                if (!isDoneSet) {
                    authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                        balanceViewModel.setBalance(it!!, args.workspace.id.toString(), 1, "Done")
                        balanceViewModel.setBalance(it, args.workspace.id.toString(), 0, "Done")
                        balanceViewModel.setOverviewBalance(it, args.workspace.id.toString())
                    })
                }
                balanceViewModel.getIncomeDoneBalance().observe(viewLifecycleOwner, {
                    balanceIncomeAdapter.setData(it)
                })

                balanceViewModel.getOutcomeDoneBalance().observe(viewLifecycleOwner, {
                    balanceOutcomeAdapter.setData(it)
                })
            }
            binding.btnPlannedMoneyReport.isChecked -> {
                if (!isPlannedSet) {
                    authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                        balanceViewModel.setBalance(
                            it!!,
                            args.workspace.id.toString(),
                            1,
                            "Planned"
                        )
                        balanceViewModel.setBalance(it, args.workspace.id.toString(), 0, "Planned")
                        balanceViewModel.setOverviewBalance(it, args.workspace.id.toString())
                    })
                }

                balanceViewModel.getIncomePlannedBalance().observe(viewLifecycleOwner, {
                    balanceIncomeAdapter.setData(it)
                })

                balanceViewModel.getOutcomePlannedBalance().observe(viewLifecycleOwner, {
                    balanceOutcomeAdapter.setData(it)
                })
            }
        }

        rvIncomeBalance.adapter = balanceIncomeAdapter
        rvOutcomeBalance.adapter = balanceOutcomeAdapter
    }

    private fun setupOverview() {
        when {
            binding.btnAllMoneyReport.isChecked -> {
                if (!isAllSet) {
                    authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                        balanceViewModel.setOverviewBalance(it!!, args.workspace.id.toString())
                    })
                }
                balanceViewModel.getOverviewBalance().observe(viewLifecycleOwner, {
                    binding.tvNominalIncomeOverviewMoneyReport.text =
                        it.incomeBalance.toCurrencyFormat()
                    binding.tvNominalOutcomeOverviewMoneyReport.text =
                        it.outcomeBalance.toCurrencyFormat()
                    binding.tvNominalBalanceOverviewMoneyReport.text =
                        it.totalBalance.toCurrencyFormat()
                })
            }
            binding.btnDoneMoneyReport.isChecked -> {
                if (!isDoneSet) {
                    authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                        balanceViewModel.setOverviewBalance(
                            it!!,
                            args.workspace.id.toString(),
                            "Done"
                        )
                    })
                }
                balanceViewModel.getOverviewDoneBalance().observe(viewLifecycleOwner, {
                    binding.tvNominalIncomeOverviewMoneyReport.text =
                        it.incomeBalance.toCurrencyFormat()
                    binding.tvNominalOutcomeOverviewMoneyReport.text =
                        it.outcomeBalance.toCurrencyFormat()
                    binding.tvNominalBalanceOverviewMoneyReport.text =
                        it.totalBalance.toCurrencyFormat()
                })
            }
            binding.btnPlannedMoneyReport.isChecked -> {
                if (!isPlannedSet) {
                    authViewModel.getAuthToken().observe(viewLifecycleOwner, {
                        balanceViewModel.setOverviewBalance(
                            it!!,
                            args.workspace.id.toString(),
                            "Planned"
                        )
                    })
                }
                balanceViewModel.getOverviewPlannedBalance().observe(viewLifecycleOwner, {
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
                when{
                    binding.btnAllMoneyReport.isChecked -> toDetailMoneyReport(1)
                    binding.btnPlannedMoneyReport.isChecked -> toDetailMoneyReport(1, "Planned")
                    binding.btnDoneMoneyReport.isChecked -> toDetailMoneyReport(1, "Done")
                }
            }
            binding.tvOutcomeDetailMoneyReport -> {
                when{
                    binding.btnAllMoneyReport.isChecked -> toDetailMoneyReport(0)
                    binding.btnPlannedMoneyReport.isChecked -> toDetailMoneyReport(0, "Planned")
                    binding.btnDoneMoneyReport.isChecked -> toDetailMoneyReport(0, "Done")
                }
            }
            binding.btnAllMoneyReport -> {
                setupDataAdapter()
                setupOverview()
                isAllSet = true
            }
            binding.btnDoneMoneyReport -> {
                setupDataAdapter()
                setupOverview()
                isDoneSet = true
            }
            binding.btnPlannedMoneyReport -> {
                setupDataAdapter()
                setupOverview()
                isPlannedSet = true
            }
        }
    }

    private fun toDetailMoneyReport(isIncome: Int, status: String? = null) {
        val action =
            MoneyReportFragmentDirections.actionMoneyReportFragmentToDetailMoneyReportFragment(
                isIncome,
                args.workspace,
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
}