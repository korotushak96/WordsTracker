package com.example.wordstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wordstracker.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MyDictionaryApp
        val dao = app.database.wordDao()
        enableEdgeToEdge()
        setContent {
            MainScreen(dao)
        }
    }
}
