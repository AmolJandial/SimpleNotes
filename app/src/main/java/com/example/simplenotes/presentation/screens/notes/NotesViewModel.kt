@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.simplenotes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenotes.data.TestNotesRepositoryImpl
import com.example.simplenotes.domain.GetAllNotesUseCase
import com.example.simplenotes.domain.Note
import com.example.simplenotes.domain.SearchNoteUseCase
import com.example.simplenotes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {
    private val repository = TestNotesRepositoryImpl

    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val searchNotesUseCase = SearchNoteUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow<NotesScreenState>(NotesScreenState())
    val state = _state.asStateFlow()


    init {

        query.onEach { q ->
            _state.update { it.copy(query = q) }
        }.flatMapLatest { q ->
            if (q.isBlank()) {
                getAllNotesUseCase()
            } else {
                searchNotesUseCase(q)
            }
        }.onEach { notes ->
            val pinnedNotes = notes.filter { it.isPinned }
            val otherNotes = notes.filter { !it.isPinned }
            _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
        }.launchIn(viewModelScope)
    }


    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {


                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                is NotesCommand.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }

    }


}

sealed interface NotesCommand {
    data class InputSearchQuery(val query: String) : NotesCommand
    data class SwitchPinnedStatus(val noteId: Int) : NotesCommand

}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)