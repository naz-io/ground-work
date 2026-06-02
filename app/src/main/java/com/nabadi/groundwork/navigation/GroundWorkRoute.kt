package com.nabadi.groundwork.navigation

import com.nabadi.groundwork.domain.model.FieldNoteId

object GroundWorkRoute {
    const val FIELD_NOTES_LIST = "field_notes_list"

    const val FIELD_NOTE_EDITOR = "field_note_editor"
    const val FIELD_NOTE_ID_ARG = "fieldNoteId"
    const val FIELD_NOTE_EDITOR_ROUTE = "$FIELD_NOTE_EDITOR?$FIELD_NOTE_ID_ARG={$FIELD_NOTE_ID_ARG}"

    fun fieldNoteEditor(fieldNoteId: FieldNoteId? = null): String =
        if (fieldNoteId == null) {
            FIELD_NOTE_EDITOR
        } else {
            "$FIELD_NOTE_EDITOR?$FIELD_NOTE_ID_ARG=${fieldNoteId.value}"
        }
}