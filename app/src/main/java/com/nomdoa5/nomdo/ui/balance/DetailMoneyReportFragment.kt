package com.nomdoa5.nomdo.ui.balance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.FragmentDetailMoneyReportBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.helpers.adapter.BalanceAdapter
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.board.BoardViewModel
import kotlinx.coroutines.flow.collect

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DetailMoneyReportFragment : Fragment(), BalanceAdapter.OnBalanceClickListener,
    SwipeRefreshLayout.OnRefreshListener {
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
        binding.swipeDetailMoneyReport.setOnRefreshListener(this)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            balanceViewModel.balanceState.collect {
                when (it) {
                    is LoadingState.Success -> {
                        binding.swipeDetailMoneyReport.isRefreshing = false
                    }
                    is LoadingState.Loading -> {
                        binding.swipeDetailMoneyReport.isRefreshing = true
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.setupFabMargin(0)
    }

    private fun setupRecyclerView() {
        balanceAdapter = BalanceAdapter(requireContext(), this)
        rvBalance = requireView().findViewById(R.id.rv_type_detail_balance)
        rvBalance.setHasFixedSize(true)
        rvBalance.layoutManager = LinearLayoutManager(context)

        setupDataAdapter()

        if (args.isIncome == 1) {
            binding.tvTypeDetailMoneyReport.text = "Income"
            binding.tvTypeDetailMoneyReport.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary
                )
            )
            binding.tvTypeDetailMoneyReport.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_income),
                null,
                null,
                null
            )
            when (args.status) {
                "Planned" -> {
                    balanceViewModel.getIncomePlannedBalance().observe(viewLifecycleOwner, {
                        balanceAdapter.setData(it)
                    })
                }
                "Done" -> {
                    balanceViewModel.getIncomeDoneBalance().observe(viewLifecycleOwner, {
                        balanceAdapter.setData(it)
                    })
                }
                else -> {
                    balanceViewModel.getIncomeBalance().observe(viewLifecycleOwner, {
                        balanceAdapter.setData(it)
                    })
                }
            }
        } else {
            binding.tvTypeDetailMoneyReport.text = "Outcome"
            binding.tvTypeDetailMoneyReport.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.accent
                )
            )
            binding.tvTypeDetailMoneyReport.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_outcome),
                null,
                null,
                null
            )
            when (args.status) {
                "Planned" -> {
                    balanceViewModel.getOutcomePlannedBalance().observe(viewLifecycleOwner, {
                        balanceAdapter.setData(it)
                    })
                }
                "Done" -> {
                    balanceViewModel.getOutcomeDoneBalance().observe(viewLifecycleOwner, {
                        balanceAdapter.setData(it)
                    })
                }
                else -> {
                    balanceViewModel.getOutcomeBalance().observe(viewLifecycleOwner, {
                        balanceAdapter.setData(it)
                    })
                }
            }
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

    override fun onBalanceClick(data: Balance) {
        val updateBalanceDialogFragment = UpdateBalanceDialogFragment()
        val bundle = Bundle()
        bundle.putParcelable("EXTRA_BALANCE", data)
        updateBalanceDialogFragment.arguments = bundle
        updateBalanceDialogFragment.show(requireActivity().supportFragmentManager, "Update Balance")
    }

    private fun setupDataAdapter(){
        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            balanceViewModel.setBalance(
                it!!,
                args.workspace.id.toString(),
                args.isIncome,
                args.status
            )
        })
    }

    override fun onRefresh() {
        dispatchRefresh()
    }

    fun dispatchRefresh() {
        setupDataAdapter()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}