package com.nabadi.groundwork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nabadi.groundwork.feature.fieldnotes.FieldNoteEditorRoute
import com.nabadi.groundwork.feature.fieldnotes.FieldNotesListRoute

@Composable
fun GroundWorkNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = GroundWorkRoute.FIELD_NOTES_LIST,
        modifier = modifier,
    ) {
        composable(route = GroundWorkRoute.FIELD_NOTES_LIST) {
            FieldNotesListRoute(
                onAddFieldNoteClick = {
                    navController.navigate(GroundWorkRoute.FIELD_NOTE_EDITOR)
                }
            )
        }

        composable(route = GroundWorkRoute.FIELD_NOTE_EDITOR) {
            FieldNoteEditorRoute(
                onFieldNoteSaved = {},
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}