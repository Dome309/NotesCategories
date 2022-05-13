package com.example.notescategories.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notescategories.model.Note

@Database(entities = [Note::class], version = 5, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    companion object {
        var noteDatabase: NoteDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): NoteDatabase {
            if (noteDatabase == null) {
                noteDatabase = Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "note.db"
                ).createFromAsset("note.db").allowMainThreadQueries().build()
            }

                return noteDatabase!!
            }
       }
    abstract fun noteDao(): NoteDao
}