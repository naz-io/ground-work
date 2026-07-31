package com.nabadi.groundwork.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PersistedEnumDecodingTest {

    @Test
    fun `site priority maps unknown value to normal`() {
        assertEquals(SitePriority.NORMAL, SitePriority.fromStorage("CRITICAL"))
    }

    @Test
    fun `site status maps unknown value to archived`() {
        assertEquals(SiteStatus.ARCHIVED, SiteStatus.fromStorage("ON_HOLD"))
    }

    @Test
    fun `field note status maps unknown value to archived`() {
        assertEquals(FieldNoteStatus.ARCHIVED, FieldNoteStatus.fromStorage("REVIEW"))
    }
}
