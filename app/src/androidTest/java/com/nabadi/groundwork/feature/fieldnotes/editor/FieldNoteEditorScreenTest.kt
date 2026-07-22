package com.nabadi.groundwork.feature.fieldnotes.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FieldNoteEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun associatedSiteSelector_assignsAndClearsSite() {
        val siteId = SiteId("site-001")
        var selectedSiteId by mutableStateOf<SiteId?>(null)

        composeTestRule.setContent {
            GroundWorkTheme {
                FieldNoteEditorScreen(
                    uiState = FieldNoteEditorUiState(
                        siteId = selectedSiteId,
                        availableSites = listOf(
                            SiteOptionUiState(
                                id = siteId,
                                name = "North Warehouse",
                            ),
                        ),
                    ),
                    onTitleChange = {},
                    onAssociatedSiteChange = { selectedSiteId = it },
                    onStatusChange = {},
                    onBodyChange = {},
                    onSaveClick = {},
                    onDeleteClick = {},
                    onDiscardClick = {},
                    onBackClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag(FIELD_NOTE_SITE_SELECTOR_TAG).performClick()
        composeTestRule.onNodeWithTag(fieldNoteSiteOptionTag(siteId)).performClick()

        composeTestRule.runOnIdle {
            assertEquals(siteId, selectedSiteId)
        }
        composeTestRule.onNodeWithText("North Warehouse").assertIsDisplayed()

        composeTestRule.onNodeWithTag(FIELD_NOTE_SITE_SELECTOR_TAG).performClick()
        composeTestRule.onNodeWithTag(FIELD_NOTE_UNASSIGNED_SITE_OPTION_TAG).performClick()

        composeTestRule.runOnIdle {
            assertNull(selectedSiteId)
        }
        composeTestRule.onNodeWithText("None / Unassigned").assertIsDisplayed()
    }

    @Test
    fun siteOptionsError_preservesSelectedSiteLabelAndShowsError() {
        val siteId = SiteId("site-existing")

        composeTestRule.setContent {
            GroundWorkTheme {
                FieldNoteEditorScreen(
                    uiState = FieldNoteEditorUiState(
                        siteId = siteId,
                        siteOptionsErrorMessage = "Unable to load sites.",
                    ),
                    onTitleChange = {},
                    onAssociatedSiteChange = {},
                    onStatusChange = {},
                    onBodyChange = {},
                    onSaveClick = {},
                    onDeleteClick = {},
                    onDiscardClick = {},
                    onBackClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText(siteId.value).assertIsDisplayed()
        composeTestRule.onNodeWithText("Unable to load sites.").assertIsDisplayed()
    }
}
