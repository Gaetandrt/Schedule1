package com.example.schedule1.presentation.add_edit_routine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.schedule1.databinding.FragmentAddEditRoutineBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        setupClickListeners()
        observeViewModelEvents()

        // TODO: Pre-populate fields if editing (using arguments and SavedStateHandle)
    }

    private fun setupClickListeners(): Unit {
        binding.buttonSave.setOnClickListener {
            val name: String = binding.editTextName.text.toString().trim()
            val description: String? = binding.editTextDescription.text.toString().trim()
            val time: String = binding.editTextTime.text.toString().trim()
            viewModel.saveRoutine(name, description, time)
        }
    }

    private fun observeViewModelEvents(): Unit {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AddEditRoutineEvent.SaveSuccess -> {
                            Toast.makeText(context, "Routine saved", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp() // Go back to home screen
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