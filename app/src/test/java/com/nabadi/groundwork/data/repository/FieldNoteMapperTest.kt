package com.nabadi.groundwork.data.repository

import com.nabadi.groundwork.data.local.FieldNoteEntity
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteId
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.domain.model.SiteId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FieldNoteMapperTest {
    @Test
    fun `toDomain maps FieldNoteEntity to domain model`() {
        val entity = FieldNoteEntity(
            id = "field-note-001",
            siteId = "site-001",
            title = "North gate safety check",
            body = "Loose temporary fencing reported near the north access point.",
            status = "ACTIVE",
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )
        val fieldNote = entity.toDomain()

        assertEquals(FieldNoteId("field-note-001"), fieldNote.id)
        assertEquals(SiteId("site-001"), fieldNote.siteId)
        assertEquals("North gate safety check", fieldNote.title)
        assertEquals(
            "Loose temporary fencing reported near the north access point.",
            fieldNote.body
        )
        assertEquals(FieldNoteStatus.ACTIVE, fieldNote.status)
        assertEquals(1_734_220_800_000L, fieldNote.createdAt)
        assertEquals(1_734_224_400_000L, fieldNote.updatedAt)
    }

    @Test
    fun `toEntity maps domain model to FieldNoteEntity`() {
        val fieldNote = FieldNote(
            id = FieldNoteId("field-note-001"),
            siteId = SiteId("site-001"),
            title = "North gate safety check",
            body = "Loose temporary fencing reported near the north access point.",
            status = FieldNoteStatus.ACTIVE,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )

        val entity = fieldNote.toEntity()

        assertEquals("field-note-001", entity.id)
        assertEquals("site-001", entity.siteId)
        assertEquals("North gate safety check", entity.title)
        assertEquals("Loose temporary fencing reported near the north access point.", entity.body)
        assertEquals("ACTIVE", entity.status)
        assertEquals(1_734_220_800_000L, entity.createdAt)
        assertEquals(1_734_224_400_000L, entity.updatedAt)
    }

    @Test
    fun `toDomain maps null site id to null`() {
        val entity = FieldNoteEntity(
            id = "field-note-003",
            siteId = null,
            title = "Unassigned note",
            body = "Captured quickly before selecting a site.",
            status = "DRAFT",
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )

        val fieldNote = entity.toDomain()

        assertNull(fieldNote.siteId)
    }

    @Test
    fun `toEntity maps null site id to null`() {
        val fieldNote = FieldNote(
            id = FieldNoteId("field-note-004"),
            siteId = null,
            title = "Unassigned note",
            body = "Captured quickly before selecting a site.",
            status = FieldNoteStatus.DRAFT,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )

        val entity = fieldNote.toEntity()

        assertNull(entity.siteId)
    }

    @Test
    fun `toEntity maps archived status to entity status value`() {
        val fieldNote = FieldNote(
            id = FieldNoteId("field-note-002"),
            siteId = SiteId("site-002"),
            title = "Archived equipment note",
            body = "Resolved generator inspection note.",
            status = FieldNoteStatus.ARCHIVED,
            createdAt = 1_734_220_800_000L,
            updatedAt = 1_734_224_400_000L,
        )

        val entity = fieldNote.toEntity()

        assertEquals("ARCHIVED", entity.status)
    }
}