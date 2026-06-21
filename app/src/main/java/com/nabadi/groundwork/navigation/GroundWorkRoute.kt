package com.nabadi.groundwork.navigation

import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId

object GroundWorkRoute {
    const val FIELD_NOTES_LIST = "field_notes_list"
    const val FIELD_NOTE_EDITOR = "field_note_editor"
    const val FIELD_NOTE_ID_ARG = "fieldNoteId"
    const val FIELD_NOTE_EDITOR_ROUTE = "$FIELD_NOTE_EDITOR?$FIELD_NOTE_ID_ARG={$FIELD_NOTE_ID_ARG}"

    const val SITES_LIST = "sites_list"
    const val SITE_EDITOR = "site_editor"
    const val SITE_ID_ARG = "siteId"
    const val SITE_EDITOR_ROUTE = "$SITE_EDITOR?$SITE_ID_ARG={$SITE_ID_ARG}"


    fun fieldNoteEditor(fieldNoteId: FieldNoteId? = null): String =
        if (fieldNoteId == null) {
            FIELD_NOTE_EDITOR
        } else {
            "$FIELD_NOTE_EDITOR?$FIELD_NOTE_ID_ARG=${fieldNoteId.value}"
        }

    fun siteEditor(siteId: SiteId? = null): String =
        if (siteId == null) {
            SITE_EDITOR
        } else {
            "$SITE_EDITOR?$SITE_ID_ARG=${siteId.value}"
        }
}