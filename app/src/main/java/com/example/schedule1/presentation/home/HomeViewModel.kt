package com.example.schedule1.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schedule1.domain.model.Routine
import com.example.schedule1.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val routines: List<Routine> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    routineRepository: RoutineRepository
) : ViewModel() {

    // Observe routines from the repository and map to UI state
    val uiState: StateFlow<HomeUiState> = routineRepository.observeAllRoutines()
        .map { routines -> HomeUiState(routines = routines, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // Keep flow active for 5s
            initialValue = HomeUiState(isLoading = true) // Initial state while loading
        )

    // Events/Actions can be added here if needed (e.g., delete routine)
}