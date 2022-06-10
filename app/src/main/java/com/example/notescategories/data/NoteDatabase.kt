package com.example.notescategories.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notescategories.entity.Note

@Database(entities = [Note::class], version = 5, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    companion object {
        var noteDatabase: NoteDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): NoteDatabase {
            if (noteDatabase == null) {
                noteDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note.db"
                ).build()
            }

                return noteDatabase!!
            }
       }
    abstract fun noteDao(): NoteDao
}