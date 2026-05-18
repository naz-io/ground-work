package com.nabadi.groundwork.domain.repository

import com.nabadi.groundwork.domain.model.Note
import com.nabadi.groundwork.domain.model.NoteId
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>

    suspend fun getNote(id: NoteId): Note?

    suspend fun saveNote(note: Note)

    suspend fun deleteNote(id: NoteId)
}