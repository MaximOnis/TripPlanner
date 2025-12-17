package com.example.tripplanner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.tripplanner.TripPlannerApp
import com.example.tripplanner.data.entity.Note
import com.example.tripplanner.ui.viewmodel.NoteViewModel

@Composable
fun TripNoteScreen(navController: NavHostController, tripId: Long) {
    val app = LocalContext.current.applicationContext as TripPlannerApp
    val viewModel = remember { NoteViewModel(app.repository) }
    val notes by viewModel.notes.collectAsState()
    val newNote = remember { mutableStateOf("") }
    LaunchedEffect(tripId) { viewModel.loadTrip(tripId) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = newNote.value,
            onValueChange = { newNote.value = it },
            label = { Text("Додати нотатку") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            viewModel.save(noteId = null, content = newNote.value, photoUri = null)
            newNote.value = ""
        }) { Text("Зберегти нотатку") }
        LazyColumn {
            items(notes) { note -> NoteItem(note = note, onDelete = { viewModel.delete(note) }) }
        }
        Button(onClick = { navController.popBackStack() }) { Text("Скасувати") }
    }
}

@Composable
private fun NoteItem(note: Note, onDelete: () -> Unit) {
    Column(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        Text(note.content)
        note.photoUri?.let { uri ->
            Image(painter = rememberAsyncImagePainter(uri), contentDescription = null)
        }
        Button(onClick = onDelete) { Text("Видалити") }
    }
}
