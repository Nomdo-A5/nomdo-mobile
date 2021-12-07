package com.nomdoa5.nomdo.ui.balance

import NoFilterAdapter
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import com.nomdoa5.nomdo.helpers.ViewModelFactory
import com.nomdoa5.nomdo.repository.local.UserPreferences
import com.nomdoa5.nomdo.repository.model.request.balance.BalanceRequest
import com.nomdoa5.nomdo.ui.auth.AuthViewModel
import com.nomdoa5.nomdo.ui.workspace.WorkspacesViewModel
import java.util.*
import kotlin.collections.ArrayList
import android.provider.OpenableColumns
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nomdoa5.nomdo.helpers.LoadingState
import com.nomdoa5.nomdo.helpers.getFileName
import com.nomdoa5.nomdo.helpers.snackbar
import com.nomdoa5.nomdo.repository.model.request.AttachmentRequestBody
import com.nomdoa5.nomdo.ui.MainActivity
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class CreateBalanceDialogFragment : DialogFragment(), View.OnClickListener, View.OnTouchListener,
    AttachmentRequestBody.UploadCallback {
    private var _binding: DialogFragmentCreateBalanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var workspacesViewModel: WorkspacesViewModel
    private lateinit var authViewModel: AuthViewModel
    private var date: String? = null
    private var spinnerWorkspacePosition: Int? = null
    private val workspaceAdapterId = ArrayList<String>()
    private var selectedImageUri: Uri? = null
    private val downloadId: Long = 0

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
        binding.editAttachmentAddBalance.setOnTouchListener(this)
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
                val isIncome = if (type == "Income") {
                    1
                } else {
                    0
                }
                val status = binding.spinnerStatusAddBalance.text.toString()

                val balance =
                    BalanceRequest(workspaceId, nominal, description, isIncome, status, date)
                authViewModel.getAuthToken().observe(this, {
                    balanceViewModel.addBalance(it!!, balance)
                })

//                balanceViewModel.getAddBalanceState().observe(this, {
//                    if (it) {
//                        Toast.makeText(requireContext(), "Balance Added", Toast.LENGTH_SHORT).show()
//                        binding.btnAddBalance.doneLoadingAnimation(
//                            resources.getColor(R.color.teal_200),
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!
//                                .toBitmap()
//                        )
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            dismiss()
//                        }, 1000)
//                    } else {
//                        binding.btnAddBalance.revertAnimation()
//                        Toast.makeText(requireContext(), "Add Balance Failed!!", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                })



                balanceViewModel.getAddBalanceResponse().observe(this, {
                    if (it != null) {
                        uploadImage(it.id.toString())
                    }
//                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
//                        binding.btnAddBalance.doneLoadingAnimation(
//                            resources.getColor(R.color.teal_200),
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)!!
//                                .toBitmap()
//                        )
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            dismiss()
//                        }, 1000)
//                    } else {
//                        binding.btnAddBalance.revertAnimation()
//                        Toast.makeText(requireContext(), "Add Balance Failed!!", Toast.LENGTH_SHORT)
//                            .show()
//                    }
                })

                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    balanceViewModel.balanceState.collect {
                        when(it){
                            is LoadingState.Loading -> {
                                binding.btnAddBalance.startAnimation()
                            }
                            is LoadingState.Success -> {
                                (activity as MainActivity?)!!.showSnackbar("Balance Created")
                                dismiss()
                            }
                            is LoadingState.Error -> {
                                binding.btnAddBalance.revertAnimation()
                                showSnackbar(it.message)
                            }
                            else -> Unit
                        }
                    }
                }
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

    fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
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
            when (v) {
                binding.editDateAddBalance -> {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    val dpd = DatePickerDialog(
                        requireActivity(), { _, y, m, d ->
                            date = "$y-$m-$d"
                            binding.editDateAddBalance.setText(date)
                        },
                        year,
                        month,
                        day
                    )

                    dpd.show()
                }
                binding.editAttachmentAddBalance -> {
                    openImageChooser()
                }
            }
        }
        return false
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, UploadAttachmentDialogFragment.REQUEST_CODE_PICK_IMAGE)
        }
    }

    private fun uploadImage(balanceId: String) {
        if (selectedImageUri == null) {
            requireView().rootView.snackbar("Select an Image First")
            return
        }

        val parcelFileDescriptor =
            requireActivity().contentResolver.openFileDescriptor(selectedImageUri!!, "r", null)
                ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(
            requireActivity().cacheDir,
            requireActivity().contentResolver.getFileName(selectedImageUri!!)
        )
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val body = AttachmentRequestBody(file, "image", this)

        authViewModel.getAuthToken().observe(viewLifecycleOwner, {
            balanceViewModel.addAttachment(
                it!!,
                MultipartBody.Part.createFormData(
                    "file_path",
                    file.name,
                    body
                ),
                balanceId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                UploadAttachmentDialogFragment.REQUEST_CODE_PICK_IMAGE -> {
                    val uri = data?.data
                    val cursor =
                        requireActivity().contentResolver.query(uri!!, null, null, null, null)
                    val nameIndex: Int = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    selectedImageUri = data.data
                    val name: String = cursor.getString(nameIndex)
                    val size = java.lang.Long.toString(cursor.getLong(sizeIndex))
                    binding.editAttachmentAddBalance.setText(name)
                }
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {

    }
}