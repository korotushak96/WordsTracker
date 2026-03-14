package com.example.wordstracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wordstracker.Dao.WordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WordViewModel(private val dao: WordDao) : ViewModel() {
    var englishWord by mutableStateOf("")
        private set
    var translateWord by mutableStateOf("")
        private set

    var showDeleteDialog by mutableStateOf(false)
        private set

    var wordEdit: VocabWord? by mutableStateOf(null)
        private set

    val wordsList: Flow<List<VocabWord>> = dao.getAllWords()

    fun updateEnglishWord(newWord: String) {
        englishWord = newWord
    }

    fun updateTranslateWord(newWord: String) {
        translateWord = newWord
    }

    fun addWord() {
        if (englishWord.isNotEmpty() && translateWord.isNotEmpty()) {
            val newWord = VocabWord(word = englishWord, translation = translateWord)

            viewModelScope.launch(Dispatchers.IO) {
                dao.insertWord(newWord)
            }

            englishWord = ""
            translateWord = ""
        }
    }

    fun deleteLearnedWords() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteLearnedWords()
        }
        showDeleteDialog = false
    }

    fun changeWordState(word: VocabWord, isLearned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertWord(word.copy(isLearned = isLearned))
        }
    }

    fun deleteWord(word: VocabWord) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWord(word)
        }
    }

    fun onShowDeleteDialog() {
        showDeleteDialog = true
    }

    fun hideDeleteDialog() {
        showDeleteDialog = false
    }

    fun onWordEditSheet(editWord: VocabWord) {
        wordEdit = editWord
        englishWord = editWord.word
        translateWord = editWord.translation
    }

    fun hideWordEditSheet() {
        wordEdit = null
        englishWord = ""
        translateWord = ""
    }

    fun saveEditWord() {
        val currentWord = wordEdit ?: return

        val wordToSave = englishWord
        val translationToSave = translateWord

        viewModelScope.launch(Dispatchers.IO) {
            dao.insertWord(currentWord.copy(word = wordToSave, translation = translationToSave))
        }
        hideWordEditSheet()
    }

}


class WordViewModelFactory(private val dao: WordDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}