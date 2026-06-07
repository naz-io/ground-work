package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.repository.FieldNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFieldNoteRepository() : FieldNoteRepository {

    private val fieldNotesFlow = MutableStateFlow<Map<FieldNoteId, FieldNote>>(emptyMap())
    private var shouldThrowError = false

    override fun observeFieldNotes(): Flow<List<FieldNote>> {
        return fieldNotesFlow.map {
            if (shouldThrowError) throw Exception("Test Error")
            it.values.toList()
        }
    }

    override suspend fun getFieldNote(id: FieldNoteId): FieldNote? {
        if (shouldThrowError) throw Exception("Test Error")
        return fieldNotesFlow.value[id]
    }

    override suspend fun saveFieldNote(fieldNote: FieldNote) {
        if (shouldThrowError) throw Exception("Test Error")
        fieldNotesFlow.update { it + (fieldNote.id to fieldNote) }
    }

    override suspend fun deleteFieldNote(id: FieldNoteId) {
        if (shouldThrowError) throw Exception("Test Error")
        fieldNotesFlow.update { it - id }
    }

    fun setFieldNotes(fieldNotes: List<FieldNote>) {
        fieldNotesFlow.value = fieldNotes.associateBy { it.id }
    }

    fun setShouldThrowError(value: Boolean) {
        shouldThrowError = value
    }
}