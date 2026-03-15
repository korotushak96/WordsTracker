package com.example.wordstracker

import android.app.Application
import com.example.wordstracker.data.AppDatabase


class MyDictionaryApp: Application(){
    val database by lazy { AppDatabase.getDatabase(this) }
}