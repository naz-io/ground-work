package com.nabadi.groundwork.feature.sites.editor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nabadi.groundwork.ui.theme.GroundWorkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SiteEditorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nameRequiredHint_isShownWhenEditedNameIsBlank() {
        setScreen(SiteEditorUiState(name = "", isNameEdited = true))

        composeTestRule.onNodeWithText(NAME_REQUIRED).assertIsDisplayed()
    }

    @Test
    fun nameRequiredHint_isHiddenBeforeNameIsEdited() {
        setScreen(SiteEditorUiState(name = ""))

        composeTestRule.onNodeWithText(NAME_REQUIRED).assertDoesNotExist()
    }

    private fun setScreen(uiState: SiteEditorUiState) {
        composeTestRule.setContent {
            GroundWorkTheme {
                SiteEditorScreen(
                    uiState = uiState,
                    onNameChange = {},
                    onDescriptionChange = {},
                    onLocationChange = {},
                    onStatusChange = {},
                    onPriorityChange = {},
                    onSaveClick = {},
                    onDestructiveActionClick = {},
                    onBackClick = {},
                )
            }
        }
    }
}

private const val NAME_REQUIRED = "Site name is required"
