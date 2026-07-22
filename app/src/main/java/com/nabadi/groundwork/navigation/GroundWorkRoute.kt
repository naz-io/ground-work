package com.nabadi.groundwork.navigation

import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.SiteId

object GroundWorkRoute {
    const val FIELD_NOTES_LIST = "field_notes_list"
    const val FIELD_NOTE_EDITOR = "field_note_editor"
    const val FIELD_NOTE_ID_ARG = "fieldNoteId"
    const val SITE_ID_ARG = "siteId"
    const val FIELD_NOTE_EDITOR_ROUTE =
        "$FIELD_NOTE_EDITOR?$FIELD_NOTE_ID_ARG={$FIELD_NOTE_ID_ARG}&$SITE_ID_ARG={$SITE_ID_ARG}"

    const val SITES_LIST = "sites_list"
    const val SITE_EDITOR = "site_editor"
    const val SITE_EDITOR_ROUTE = "$SITE_EDITOR?$SITE_ID_ARG={$SITE_ID_ARG}"

    const val SITE_DETAIL = "site_detail"
    const val SITE_DETAIL_ROUTE = "$SITE_DETAIL?$SITE_ID_ARG={$SITE_ID_ARG}"

    fun fieldNoteEditor(
        fieldNoteId: FieldNoteId? = null,
        siteId: SiteId? = null,
    ): String = buildString {
        append(FIELD_NOTE_EDITOR)
        val arguments = listOfNotNull(
            fieldNoteId?.let { "$FIELD_NOTE_ID_ARG=${it.value}" },
            siteId?.let { "$SITE_ID_ARG=${it.value}" },
        )
        if (arguments.isNotEmpty()) append("?${arguments.joinToString("&")}")
    }

    fun siteEditor(siteId: SiteId? = null): String =
        if (siteId == null) {
            SITE_EDITOR
        } else {
            "$SITE_EDITOR?$SITE_ID_ARG=${siteId.value}"
        }

    fun siteDetail(siteId: SiteId): String =
        "$SITE_DETAIL?$SITE_ID_ARG=${siteId.value}"
}
