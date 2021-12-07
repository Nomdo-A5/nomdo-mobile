package com.nomdoa5.nomdo.ui.balance

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateBalanceBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UpdateBalanceDialogFragment : DialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentUpdateBalanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var balance: Balance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentUpdateBalanceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balance = requireArguments().getParcelable("EXTRA_BALANCE")!!
//        binding.editTitleUpdateBalance.setText(balance.balanceName)
        binding.btnUpdateBalance.setOnClickListener(this)
//        binding.btnDeleteBalance.setOnClickListener(this)
        binding.imgCloseUpdateBalance.setOnClickListener(this)
        binding.editNominalUpdateBalance.setText(balance.nominal.toString())
        binding.editDescriptionAddBalance.setText(balance.balanceDescription)
        binding.editDateUpdateBalance.setText(balance.date)
        binding.spinnerStatusUpdateBalance.setText(balance.status)
        val type = if(balance.isIncome!! > 1) "Income" else "Outcome"
        binding.spinnerTypeUpdateBalance.setText(type)
        setupViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnUpdateBalance -> {
                binding.btnUpdateBalance.startAnimation()
                //val newBalanceTitle = binding.editNameUpdateBalance.text.toString()
//                val newBalance = UpdateBalanceRequest(balance.id, newBalanceTitle)

//                authViewModel.getAuthToken().observe(this, {
//                    balancesViewModel.updateBalance(it!!, newBalance)
//                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    balanceViewModel.balanceState.collect {
                        when(it){
                            is LoadingState.Loading -> {
                                binding.btnUpdateBalance.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Balance Updated")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnUpdateBalance.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }

            }
//            binding.btnDeleteBalance -> {
//                binding.btnDeleteBalance.startAnimation()
////                authViewModel.getAuthToken().observe(this, {
////                    balancesViewModel.deleteBalance(it!!, balance.id.toString())
////                })
//
//                balanceViewModel.getDeleteBalanceState().observe(this, {
//                    if(it){
//                        Toast.makeText(
//                            requireContext(),
//                            "Delete Balance Success",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        binding.btnUpdateBalance.doneLoadingAnimation(
//                            resources.getColor(R.color.teal_200),
//                            ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.ic_check
//                            )!!
//                                .toBitmap()
//                        )
//                        dismiss()
//                    }else{
//                        Toast.makeText(
//                            requireContext(),
//                            "Delete Balance Failed!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        binding.btnUpdateBalance.revertAnimation()
//                        dismiss()
//                    }
//                })
//                dismiss()
//            }
            binding.imgCloseUpdateBalance -> {
                dismiss()
            }
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
    }
}