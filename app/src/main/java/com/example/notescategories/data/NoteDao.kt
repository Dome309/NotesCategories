package com.example.notescategories.data

import androidx.room.*
import com.example.notescategories.model.Note

@Dao
abstract class NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    abstract fun getAllNote() : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addNote(note: Note)

    @Delete
    abstract fun deleteNote(note: Note)

}