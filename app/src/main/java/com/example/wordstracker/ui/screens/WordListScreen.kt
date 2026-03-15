package com.example.wordstracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordstracker.ui.WordViewModel
import com.example.wordstracker.ui.WordViewModelFactory
import com.example.wordstracker.data.AppDatabase
import com.example.wordstracker.ui.components.WordItemCard

@Composable
fun WordListScreen() {

    val context = LocalContext.current

    val db = AppDatabase.getDatabase(context)
    val dao = db.wordDao()

    val viewModel: WordViewModel = viewModel(
        factory = WordViewModelFactory(dao)
    )

    val wordsList by viewModel.wordsList.collectAsState(emptyList())

    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.englishWord,
                onValueChange = { viewModel.updateEnglishWord(it) },
                label = { Text("New word") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.translateWord,
                onValueChange = { viewModel.updateTranslateWord(it) },
                label = { Text("Translation") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addWord()
                }
            ) {
                Text("Add")
            }


            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    viewModel.onShowDeleteDialog()
                }
            ) {
                Text("Delete learned words")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(
                    items = wordsList,
                    key = { currentWord -> currentWord.id }
                ) { currentWord ->
                    WordItemCard(
                        wordItem = currentWord,
                        onCheckedChange = { newState ->
                            viewModel.changeWordState(currentWord, newState)
                        },
                        onDeleteClick = {
                            viewModel.deleteWord(currentWord)
                        },
                        onEditClick = {
                            viewModel.onWordEditSheet(currentWord)
                        }
                    )
                }
            }
        }

        if (viewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.hideDeleteDialog()
                },
                title = {
                    Text("Confirm deletion")
                },
                text = {
                    Text("Are you sure you want to delete all learned words? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteLearnedWords()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.hideDeleteDialog()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (viewModel.wordEdit != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.hideWordEditSheet()
                    }
            ) {
                AnimatedVisibility(
                    visible = viewModel.wordEdit != null,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                    exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                                .imePadding()
                        ) {
                            Text(
                                text = "Edit word",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = viewModel.englishWord,
                                onValueChange = { viewModel.updateEnglishWord(it) },
                                label = { Text("English word") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = viewModel.translateWord,
                                onValueChange = { viewModel.updateTranslateWord(it) },
                                label = { Text("Translation") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                    }
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.saveEditWord()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save changes")
                            }

                        }
                    }
                }
            }
        }
    }
}