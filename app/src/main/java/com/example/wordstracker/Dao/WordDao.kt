package com.example.wordstracker.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wordstracker.VocabWord
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao{

    @Query("SELECT * FROM words_table")
    fun getAllWords(): Flow<List<VocabWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: VocabWord)

    @Delete
    fun deleteWord(word: VocabWord)

    @Query(value = "DELETE  FROM words_table WHERE isLearned = 1")
    fun deleteLearnedWords()
}