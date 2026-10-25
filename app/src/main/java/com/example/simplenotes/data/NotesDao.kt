package com.example.simplenotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM Notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM Notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM Notes WHERE id = :noteId")
    suspend fun getNote(noteId: Int): NoteDbModel

    @Query("DELETE FROM Notes WHERE id == :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE Notes SET isPinned = NOT isPinned WHERE id == :noteId")
    suspend fun switchPinnedStatus(noteId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteDbModel)
}