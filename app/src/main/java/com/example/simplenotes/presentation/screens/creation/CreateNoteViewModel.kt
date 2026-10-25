package com.example.simplenotes.presentation.screens.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenotes.data.TestNotesRepositoryImpl
import com.example.simplenotes.domain.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel : ViewModel() {

    private val repository = TestNotesRepositoryImpl
    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<EditNoteState>(
        EditNoteState.Creation()
    )
    var state = _state.asStateFlow()

    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> {
                _state.update { EditNoteState.Finished }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Creation) {
                        previousState.copy(
                            content = command.content,
                            isSaveEnabled = previousState.title.isNotBlank() && command.content.isNotBlank()
                        )
                    } else {
                        EditNoteState.Creation(content = command.content)
                    }
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                            isSaveEnabled = previousState.content.isNotBlank() && command.title.isNotBlank()
                        )
                    } else {
                        EditNoteState.Creation(title = command.title)
                    }
                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {

                    _state.update { previousNoteState ->
                        if (previousNoteState is EditNoteState.Creation) {
                            val title = previousNoteState.title
                            val content = previousNoteState.content
                            addNoteUseCase(title = title, content = content)
                            EditNoteState.Finished
                        } else {
                            previousNoteState
                        }
                    }
                }
            }
        }
    }

}

sealed interface EditNoteCommand {
    data class InputTitle(val title: String) : EditNoteCommand
    data class InputContent(val content: String) : EditNoteCommand
    data object Save : EditNoteCommand
    data object Back : EditNoteCommand
}

sealed interface EditNoteState {
    data class Creation(
        val title: String = "", val content: String = "", val isSaveEnabled: Boolean = false
    ) : EditNoteState

    data object Finished : EditNoteState
}