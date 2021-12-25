package com.nomdoa5.nomdo.ui.balance

import NoFilterAdapter
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentUpdateBalanceBinding
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.Balance
import com.nomdoa5.nomdo.repository.model.request.balance.UpdateBalanceRequest
import com.nomdoa5.nomdo.ui.MainActivity
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class UpdateBalanceDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var _binding: DialogFragmentUpdateBalanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var balance: Balance
    private var date: String? = null
    private val MY_PERMISSION_REQUEST = 100

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
        binding.btnUpdateBalance.setOnClickListener(this)
        binding.btnDeleteBalance.setOnClickListener(this)
        binding.imgCloseUpdateBalance.setOnClickListener(this)
        binding.editDateUpdateBalance.setOnClickListener(this)
        binding.editAttachmentUpdateBalance.setOnClickListener(this)
        binding.editNominalUpdateBalance.setText(balance.nominal.toString())
        binding.editDescriptionUpdateBalance.setText(balance.balanceDescription)
        binding.editDateUpdateBalance.setText(balance.date)
        binding.spinnerStatusUpdateBalance.setText(balance.status)
        val type = if (balance.isIncome!! < 1) "Outcome" else "Income"
        binding.spinnerTypeUpdateBalance.setText(type)
        setupViewModel()
        setupSpinner()
        setupAttachment()
    }

    private fun setupAttachment() {
        binding.layoutAttachmentUpdateBalance.setEndIconOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST
                )
            }

            authViewModel.getAuthToken().observe(this, {
                balanceViewModel.downloadAttachment(it!!, balance.id.toString())
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnUpdateBalance -> {
                binding.btnUpdateBalance.startAnimation()
                val newDesc = binding.editDescriptionUpdateBalance.text.toString()
                val newNominal = binding.editNominalUpdateBalance.text.toString()
                val newStatus = binding.spinnerStatusUpdateBalance.text.toString()
                val type = binding.spinnerTypeUpdateBalance.text.toString()
                val isIncome = if (type == "Income") {
                    1
                } else {
                    0
                }
                val newBalance =
                    UpdateBalanceRequest(balance.id, newNominal, newDesc, isIncome, newStatus, date)

                authViewModel.getAuthToken().observe(this, {
                    balanceViewModel.updateBalance(it!!, newBalance)
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    balanceViewModel.balanceState.collect {
                        when (it) {
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
            binding.btnDeleteBalance -> {
                binding.btnDeleteBalance.startAnimation()
                authViewModel.getAuthToken().observe(this, {
                    balanceViewModel.deleteBalance(it!!, balance.id.toString())
                })

                dismiss()
            }
            binding.imgCloseUpdateBalance -> {
                dismiss()
            }
            binding.editDateUpdateBalance -> {
                setupCalendar()
            }
            binding.editAttachmentUpdateBalance -> {
                openImageChooser()
            }
        }
    }

    fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        balanceViewModel = ViewModelProvider(this)[BalanceViewModel::class.java]
    }

    private fun setupSpinner() {
        val typeBalance = arrayOf("Income", "Outcome")
        val balanceAdapter =
            NoFilterAdapter(
                requireContext(),
                R.layout.item_dropdown,
                typeBalance
            )
        binding.spinnerTypeUpdateBalance.setAdapter(balanceAdapter)

        val statusBalance = arrayOf("Planned", "Cancelled", "Done")
        val statusAdapter =
            NoFilterAdapter(
                requireContext(),
                R.layout.item_dropdown,
                statusBalance
            )
        binding.spinnerStatusUpdateBalance.setAdapter(statusAdapter)
    }

    private fun setupCalendar() {
        val c = Calendar.getInstance()
        val y = c.get(Calendar.YEAR)
        val m = c.get(Calendar.MONTH)
        val d = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireActivity(), { mDate, _, _, _ ->
                val calendar = Calendar.getInstance()
                calendar.set(mDate.year, mDate.month, mDate.dayOfMonth)
                val format = SimpleDateFormat("yyyy-MM-dd")

                date = format.format(calendar.time)
                binding.editDateUpdateBalance.setText(date)
            },
            y,
            m,
            d
        )

        dpd.show()
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, UploadAttachmentDialogFragment.REQUEST_CODE_PICK_IMAGE)
        }
    }


}