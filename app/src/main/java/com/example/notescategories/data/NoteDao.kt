package com.example.notescategories.data

import androidx.room.*
import com.example.notescategories.model.Note

@Dao
abstract class NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    abstract fun getAllNote() : List<Note>

    @Query("SELECT * FROM note_table WHERE id=:id")
    abstract fun getNote(id:Int) : Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addNote(note: Note)

    @Update
    abstract fun updateNote(note:Note)

    @Query("DELETE FROM note_table WHERE id=:id")
    abstract fun deleteCurrentNote(id:Int)

    @Delete
    abstract fun deleteNote(note: Note)

}