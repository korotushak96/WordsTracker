package com.example.wordstracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.wordstracker.data.WordDao

@Composable
fun MainScreen(dao: WordDao) {
    var currentTab by remember { mutableStateOf(AppTab.Dictionary) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Dictionary") },
                    label = { Text("Dictionary") },
                    selected = currentTab == AppTab.Dictionary,
                    onClick = { currentTab = AppTab.Dictionary }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Training") },
                    label = { Text("Training") },
                    selected = currentTab == AppTab.Training,
                    onClick = { currentTab = AppTab.Training }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentTab) {
                AppTab.Dictionary -> {
                    WordListScreen(dao)
                }

                AppTab.Training -> {
                    Text("It wll be done soon")
                }
            }
        }
    }
}

enum class AppTab {
    Dictionary,
    Training
}