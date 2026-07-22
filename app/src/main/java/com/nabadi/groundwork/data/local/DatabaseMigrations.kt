package com.nabadi.groundwork.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `field_notes_new` (
                    `id` TEXT NOT NULL,
                    `siteId` TEXT,
                    `title` TEXT NOT NULL,
                    `body` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`siteId`) REFERENCES `sites`(`id`)
                        ON UPDATE NO ACTION ON DELETE SET NULL
                )
            """.trimIndent()
        )

        db.execSQL(
            """
                INSERT INTO `field_notes_new` (
                    `id`, `siteId`, `title`, `body`, `status`, `createdAt`, `updatedAt`
                )
                SELECT
                    `id`,
                    CASE
                        WHEN `siteId` IS NULL OR EXISTS (
                            SELECT 1 FROM `sites` WHERE `sites`.`id` = `field_notes`.`siteId`
                        ) THEN `siteId`
                        ELSE NULL
                    END,
                    `title`,
                    `body`,
                    `status`,
                    `createdAt`,
                    `updatedAt`
                FROM `field_notes`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `field_notes`")
        db.execSQL("ALTER TABLE `field_notes_new` RENAME TO `field_notes`")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_field_notes_siteId` " +
                "ON `field_notes` (`siteId`)"
        )
    }
}
