package com.nabadi.groundwork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                onFieldNoteClick = { fieldNoteId ->
                    navController.navigate(GroundWorkRoute.fieldNoteEditor(fieldNoteId))
                },
                onAddFieldNoteClick = {
                    navController.navigate(GroundWorkRoute.fieldNoteEditor())
                },
            )
        }

        composable(
            route = GroundWorkRoute.FIELD_NOTE_EDITOR_ROUTE,
            arguments = listOf(
                navArgument(GroundWorkRoute.FIELD_NOTE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            FieldNoteEditorRoute(
                onFieldNoteSaved = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}