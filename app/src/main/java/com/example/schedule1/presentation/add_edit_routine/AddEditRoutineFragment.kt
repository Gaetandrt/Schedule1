package com.example.schedule1.presentation.add_edit_routine

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.schedule1.databinding.FragmentAddEditRoutineBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddEditRoutineFragment : Fragment() {

    private var _binding: FragmentAddEditRoutineBinding? = null
    private val binding: FragmentAddEditRoutineBinding get() = _binding!!

    private val viewModel: AddEditRoutineViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditRoutineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit {
        super.onViewCreated(view, savedInstanceState)

        setupInputListeners()
        setupClickListeners()
        observeViewModelState()
        observeViewModelEvents()

        // TODO: Pre-populate fields if editing (using arguments and SavedStateHandle)
    }

    private fun setupInputListeners(): Unit {
        binding.editTextName.addTextChangedListener {
            viewModel.onNameChanged(it.toString())
        }
        binding.editTextDescription.addTextChangedListener {
            viewModel.onDescriptionChanged(it.toString())
        }
    }

    private fun setupClickListeners(): Unit {
        binding.buttonSave.setOnClickListener {
            viewModel.saveRoutine()
        }

        binding.editTextTime.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog(): Unit {
        val calendar: Calendar = Calendar.getInstance()
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute: Int = calendar.get(Calendar.MINUTE)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val selectedTime: String = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            binding.editTextTime.setText(selectedTime)
            viewModel.onTimeChanged(selectedTime)
        }

        TimePickerDialog(
            requireContext(),
            timeSetListener,
            currentHour,
            currentMinute,
            true
        ).show()
    }

    private fun observeViewModelState(): Unit {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.isVisible = state.isLoading
                    if (binding.editTextName.text.toString() != state.name) {
                        binding.editTextName.setText(state.name)
                        binding.editTextName.setSelection(state.name.length)
                    }
                    if (binding.editTextDescription.text.toString() != state.description) {
                        binding.editTextDescription.setText(state.description)
                        binding.editTextDescription.setSelection(state.description.length)
                    }
                    if (binding.editTextTime.text.toString() != state.time && !binding.editTextTime.isFocused) {
                        binding.editTextTime.setText(state.time)
                    }
                }
            }
        }
    }

    private fun observeViewModelEvents(): Unit {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AddEditRoutineEvent.SaveSuccess -> {
                            Toast.makeText(context, "Routine saved", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        is AddEditRoutineEvent.ShowError -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView(): Unit {
        super.onDestroyView()
        _binding = null
    }
}