package com.example.notescategories.data

import androidx.room.*
import com.example.notescategories.entity.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    suspend fun getAllNote() : List<Note>

    @Query("SELECT * FROM note_table WHERE id=:id")
    suspend fun getNote(id:Int) : Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note:Note)

    @Query("DELETE FROM note_table WHERE id=:id")
    suspend fun deleteCurrentNote(id:Int)

    @Delete
    suspend fun deleteNote(note: Note)

}