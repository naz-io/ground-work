package com.nabadi.groundwork.feature.fieldnotes.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.TestFieldNotes
import com.nabadi.groundwork.ui.format.absoluteDateTimeFormatter
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class FieldNoteItemCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fieldNoteItemCard_formatsUpdatedTimeWithTheProvidedFormatter() {
        val note = TestFieldNotes.fieldNote(
            id = "note-1",
            updatedAt = 1_700_000_000_000L, // 2023-11-14T22:13:20Z
        )
        // A zone other than UTC proves the card uses the passed formatter, not its own.
        val tokyoFormatter = absoluteDateTimeFormatter(Locale.US, ZoneId.of("Asia/Tokyo"))

        composeTestRule.setContent {
            GroundWorkTheme {
                FieldNoteItemCard(
                    fieldNoteItem = FieldNoteListItemUiState(note = note, siteName = null),
                    dateTimeFormatter = tokyoFormatter,
                    locale = Locale.US,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("UPDATED: NOV 15, 2023 07:13").assertIsDisplayed()
    }

    @Test
    fun fieldNoteItemCard_uppercasesSiteNameWithTheProvidedLocale() {
        val note = TestFieldNotes.fieldNote(id = "note-1", siteId = "site-1")

        composeTestRule.setContent {
            GroundWorkTheme {
                FieldNoteItemCard(
                    fieldNoteItem = FieldNoteListItemUiState(note = note, siteName = "istanbul depot"),
                    dateTimeFormatter = absoluteDateTimeFormatter(Locale.US, ZoneId.of("UTC")),
                    // Turkish uppercases "i" to a dotted "İ", unlike the device's default locale.
                    locale = Locale.forLanguageTag("tr-TR"),
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("SITE: İSTANBUL DEPOT").assertIsDisplayed()
    }
}
