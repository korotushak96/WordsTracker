package com.example.wordstracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "words_table")
data class VocabWord(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val word: String,
    val translation: String,
    var isLearned: Boolean = false
)