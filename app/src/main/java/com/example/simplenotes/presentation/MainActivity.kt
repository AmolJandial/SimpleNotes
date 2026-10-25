package com.example.simplenotes.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.simplenotes.presentation.screens.creation.CreateNoteScreen
import com.example.simplenotes.presentation.ui.theme.SimpleNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleNotesTheme {
//                NotesScreen(onNoteClick = {}, onAddNoteClick = {})
                CreateNoteScreen(onFinished = {})
            }
        }
    }
}

