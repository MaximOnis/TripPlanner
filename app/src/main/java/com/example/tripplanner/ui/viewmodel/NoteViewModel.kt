package com.example.tripplanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripplanner.data.entity.Note
import com.example.tripplanner.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: TripRepository) : ViewModel() {
    private val tripIdFlow = MutableStateFlow<Long?>(null)

    val notes: StateFlow<List<Note>> = tripIdFlow
        .flatMapLatest { id -> id?.let { repository.notesForTrip(it) } ?: MutableStateFlow(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadTrip(tripId: Long) {
        tripIdFlow.value = tripId
    }

    fun save(noteId: Long?, content: String, photoUri: String? = null) {
        val tripId = tripIdFlow.value ?: return
        viewModelScope.launch {
            repository.upsertNote(
                Note(
                    noteId = noteId ?: 0,
                    content = content,
                    photoUri = photoUri,
                    tripId = tripId
                )
            )
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }
}
