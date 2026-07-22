package com.nabadi.groundwork.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroundWorkDatabaseMigrationTest {
    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = GroundWorkDatabase::class.java,
    )

    @Test
    fun `migration 1 to 2 preserves valid links and unassigns orphaned notes`() {
        migrationTestHelper.createDatabase(TEST_DATABASE, 1).apply {
            execSQL(
                """
                    INSERT INTO `sites` (
                        `id`, `name`, `description`, `location`,
                        `priority`, `status`, `createdAt`, `updatedAt`
                    ) VALUES (
                        'site-1', 'Site', '', 'Location',
                        'NORMAL', 'ACTIVE', 1, 1
                    )
                """.trimIndent()
            )
            insertFieldNote(id = "valid-note", siteId = "site-1")
            insertFieldNote(id = "orphaned-note", siteId = "missing-site")
            close()
        }

        val migratedDatabase = migrationTestHelper.runMigrationsAndValidate(
            TEST_DATABASE,
            2,
            true,
            MIGRATION_1_2,
        )

        migratedDatabase.query(
            "SELECT `id`, `siteId` FROM `field_notes` ORDER BY `id`"
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals("orphaned-note", cursor.getString(0))
            assertNull(cursor.getString(1))

            cursor.moveToNext()
            assertEquals("valid-note", cursor.getString(0))
            assertEquals("site-1", cursor.getString(1))
        }

        migratedDatabase.execSQL("PRAGMA foreign_keys = ON")
        migratedDatabase.execSQL("DELETE FROM `sites` WHERE `id` = 'site-1'")

        migratedDatabase.query(
            "SELECT `siteId` FROM `field_notes` WHERE `id` = 'valid-note'"
        ).use { cursor ->
            cursor.moveToFirst()
            assertNull(cursor.getString(0))
        }

        migratedDatabase.close()
    }

    private fun androidx.sqlite.db.SupportSQLiteDatabase.insertFieldNote(
        id: String,
        siteId: String,
    ) {
        execSQL(
            """
                INSERT INTO `field_notes` (
                    `id`, `siteId`, `title`, `body`, `status`, `createdAt`, `updatedAt`
                ) VALUES (
                    '$id', '$siteId', 'Title', 'Body', 'DRAFT', 1, 1
                )
            """.trimIndent()
        )
    }

    private companion object {
        const val TEST_DATABASE = "groundwork-migration-test"
    }
}
