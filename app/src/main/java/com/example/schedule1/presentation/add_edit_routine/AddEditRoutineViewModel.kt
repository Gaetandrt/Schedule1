package com.example.schedule1.presentation.add_edit_routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schedule1.domain.alarm.RoutineAlarmScheduler
import com.example.schedule1.domain.model.Routine
import com.example.schedule1.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddEditRoutineEvent {
    data object SaveSuccess : AddEditRoutineEvent()
    data class ShowError(val message: String) : AddEditRoutineEvent()
}

data class AddEditRoutineUiState(
    val routine: Routine? = null, // Hold the routine being edited
    val isLoading: Boolean = false,
    val name: String = "",
    val description: String = "",
    val time: String = ""
)

@HiltViewModel
class AddEditRoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val alarmScheduler: RoutineAlarmScheduler,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _eventChannel: Channel<AddEditRoutineEvent> = Channel()
    val events: Flow<AddEditRoutineEvent> = _eventChannel.receiveAsFlow()

    private val routineId: Long = savedStateHandle.get<Long>("routineId") ?: -1L
    private val isEditMode: Boolean = routineId != -1L

    private val _uiState = MutableStateFlow(AddEditRoutineUiState(isLoading = isEditMode))
    val uiState: StateFlow<AddEditRoutineUiState> = _uiState.asStateFlow()

    init {
        if (isEditMode) {
            loadRoutineData()
        }
    }

    private fun loadRoutineData(): Unit {
        viewModelScope.launch {
            routineRepository.observeRoutineById(routineId).collectLatest { routine ->
                routine?.let {
                    _uiState.update {
                        it.copy(
                            routine = routine,
                            isLoading = false,
                            name = routine.name,
                            description = routine.description ?: "",
                            time = routine.scheduleTime
                        )
                    }
                }
            }
        }
    }

    fun onNameChanged(newName: String): Unit {
        _uiState.update { it.copy(name = newName) }
    }

    fun onDescriptionChanged(newDescription: String): Unit {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onTimeChanged(newTime: String): Unit {
        _uiState.update { it.copy(time = newTime) }
    }

    fun saveRoutine(): Unit {
        val currentState = _uiState.value
        val name = currentState.name.trim()
        val description = currentState.description.trim()
        val time = currentState.time.trim()

        if (name.isBlank()) {
            sendEvent(AddEditRoutineEvent.ShowError("Name cannot be empty"))
            return
        }
        if (!time.matches(Regex("\\d{2}:\\d{2}"))) {
            sendEvent(AddEditRoutineEvent.ShowError("Time must be in HH:mm format"))
            return
        }

        viewModelScope.launch {
            try {
                val routineToSave = Routine(
                    id = if (isEditMode) routineId else 0,
                    name = name,
                    description = description.takeIf { it.isNotBlank() },
                    scheduleTime = time
                )

                val savedRoutineId: Long
                if (isEditMode) {
                    routineRepository.updateRoutine(routineToSave)
                    savedRoutineId = routineId
                } else {
                    savedRoutineId = routineRepository.addRoutine(routineToSave)
                }

                val routineForScheduling = routineToSave.copy(id = savedRoutineId)
                alarmScheduler.schedule(routineForScheduling)

                sendEvent(AddEditRoutineEvent.SaveSuccess)
            } catch (e: Exception) {
                sendEvent(AddEditRoutineEvent.ShowError("Failed to save routine: ${e.message}"))
            }
        }
    }

    private fun sendEvent(event: AddEditRoutineEvent): Unit {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }
}