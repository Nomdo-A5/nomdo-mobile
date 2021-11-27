package com.nomdoa5.nomdo.ui.balance

import NoFilterAdapter
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.nomdoa5.nomdo.R
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateBalanceBinding
import com.nomdoa5.nomdo.databinding.DialogFragmentCreateBoardBinding
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.balance.BalanceRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.repository.model.request.board.BoardRequest
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel
import android.view.MotionEvent
import com.nomdoa5.nomdo.helpers.MonthList
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class CreateBalanceDialogFragment : DialogFragment(), View.OnClickListener, View.OnTouchListener {
    private var _binding: DialogFragmentCreateBalanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel
    private var spinnerWorkspacePosition: Int? = null
    private val workspaceAdapterId = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateBalanceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupSpinner()
        binding.imgCloseAddBalance.setOnClickListener(this)
        binding.btnAddBalance.setOnClickListener(this)
        binding.editDateAddBalance.setOnTouchListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnAddBalance -> {
                binding.btnAddBalance.startAnimation()
                val workspaceId = spinnerWorkspacePosition
                val nominal = binding.editNominalAddBalance.text.toString()
                val description = binding.editDescriptionAddBalance.text.toString()
                val type = binding.spinnerTypeAddBalance.text.toString()
                val isIncome = if (type.equals("Income")) {
                    1
                } else {
                    0
                }
                val status = binding.spinnerStatusAddBalance.text.toString()

                val balance = BalanceRequest(workspaceId, nominal, description, isIncome, status)
                authViewModel.getAuthToken().observe(this, {
                    balanceViewModel.addBalance(it!!, balance)
                })

                balanceViewModel.getAddBalanceState().observe(this, {
                    if (it) {
                        Toast.makeText(requireContext(), "Balance Added", Toast.LENGTH_SHORT).show()
                        binding.btnAddBalance.doneLoadingAnimation(
                            resources.getColor(R.color.teal_200),
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!
                                .toBitmap()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            dismiss()
                        }, 1000)
                    } else {
                        binding.btnAddBalance.revertAnimation()
                        Toast.makeText(requireContext(), "Add Balance Failed!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            }
            binding.imgCloseAddBalance -> {
                dismiss()
            }
        }
    }

    fun setupViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        authViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(AuthViewModel::class.java)
        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)
        balanceViewModel = ViewModelProvider(this).get(BalanceViewModel::class.java)
    }

    fun setupSpinner() {
        authViewModel.getAuthToken().observe(this, {
            workspacesViewModel.setWorkspace(it!!)
        })

        workspacesViewModel.getWorkspace().observe(this, {
            val workspaceName = ArrayList<String>()
            for (i in it) {
                workspaceName.add(i.workspaceName!!)
                workspaceAdapterId.add(i.id.toString())
            }
            val workspaceAdapter =
                NoFilterAdapter(
                    requireContext(),
                    R.layout.item_dropdown,
                    workspaceName.toArray(Array<String?>(workspaceName.size) { null })
                )
            binding.spinnerWorkspaceAddBalance.setAdapter(workspaceAdapter)
        })

        binding.spinnerWorkspaceAddBalance.setOnItemClickListener { _, _, position, _ ->
            spinnerWorkspacePosition = workspaceAdapterId[position].toInt()
        }

        val typeBalance = arrayOf("Income", "Outcome")
        val balanceAdapter =
            NoFilterAdapter(
                requireContext(),
                R.layout.item_dropdown,
                typeBalance
            )
        binding.spinnerTypeAddBalance.setAdapter(balanceAdapter)

        val statusBalance = arrayOf("Planned", "Cancelled", "Done")
        val statusAdapter =
            NoFilterAdapter(
                requireContext(),
                R.layout.item_dropdown,
                statusBalance
            )
        binding.spinnerStatusAddBalance.setAdapter(statusAdapter)
    }

    override fun onTouch(v: View?, motionEvent: MotionEvent?): Boolean {
        if (motionEvent!!.action == MotionEvent.ACTION_UP) {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireActivity(), { _, y, m, d ->
                    val date = "$d " + MonthList.get(m) + " " + y
                    binding.editDateAddBalance.setText(date)
                },
                year,
                month,
                day
            )

            dpd.show()
        }
        return false
    }
}