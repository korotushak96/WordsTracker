package com.example.wordstracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.concurrent.Volatile
import kotlinx.coroutines.Dispatchers
import com.example.wordstracker.Dao.WordDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           WordListScreen()
        }
    }
}

@Composable
fun WordListScreen(){

    val context = LocalContext.current

    val db = AppDatabase.getDatabase(context)
    val dao = db.wordDao()

    val coroutineScope = rememberCoroutineScope()

    var englishWord by remember { mutableStateOf("") }
    var translateWord by remember { mutableStateOf("") }
    val wordsList by dao.getAllWords().collectAsState(initial = emptyList())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = englishWord,
            onValueChange = {englishWord = it},
            label = {Text("New word")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = translateWord,
            onValueChange = {translateWord = it},
            label = {Text("Translation")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (englishWord.isNotEmpty() && translateWord.isNotEmpty()){
                    val newWord = VocabWord(word = englishWord, translation = translateWord)

                    coroutineScope.launch(Dispatchers.IO) {
                        dao.insertWord(newWord)
                    }

                    englishWord = ""
                    translateWord = ""
                }

            }
        ) {
            Text("Add")
        }


        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    dao.deleteLearnedWords()
                }
            }
        ) {
            Text("Delete learned words")
        }

        LazyColumn{
            items(
                items= wordsList,
                key = { currentWord -> currentWord.id }
            ) { currentWord ->
                WordItemCard(
                    wordItem = currentWord,
                    onCheckedChange = { newState ->

                        coroutineScope.launch(Dispatchers.IO) {
                            dao.insertWord(currentWord.copy(isLearned = newState))
                        }

                    },
                    onDeleteClick = {

                        coroutineScope.launch(Dispatchers.IO) {
                            dao.deleteWord(currentWord)
                        }

                    }
                )
            }
        }
    }


}

@Composable
fun WordItemCard(
    wordItem: VocabWord,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = wordItem.word,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (wordItem.isLearned) TextDecoration.LineThrough else TextDecoration.None
                )

                Text(
                    text = wordItem.translation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Checkbox(
                checked = wordItem.isLearned,
                onCheckedChange = {isChecked ->
                    onCheckedChange(isChecked)
                }
            )

            IconButton(onClick = { onDeleteClick() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete word",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Entity(tableName = "words_table")
data class VocabWord(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val word: String,
    val translation: String,
    var isLearned: Boolean = false
)



@Database(entities = [VocabWord::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun wordDao(): WordDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "words_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}