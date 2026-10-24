package com.nabadi.groundwork.navigation

import kotlinx.serialization.Serializable

sealed interface GroundWorkRoute {
    @Serializable
    data object SitesList : GroundWorkRoute

    @Serializable
    data object FieldNotesList : GroundWorkRoute

    @Serializable
    data class SiteDetail(val siteId: String) : GroundWorkRoute

    @Serializable
    data class SiteEditor(val siteId: String? = null) : GroundWorkRoute

    @Serializable
    data class FieldNoteEditor(
        val fieldNoteId: String? = null,
        val siteId: String? = null,
    ) : GroundWorkRoute
}
