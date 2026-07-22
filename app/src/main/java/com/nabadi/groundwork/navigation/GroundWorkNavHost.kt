package com.nabadi.groundwork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nabadi.groundwork.feature.fieldnotes.editor.FieldNoteEditorRoute
import com.nabadi.groundwork.feature.fieldnotes.list.FieldNotesListRoute
import com.nabadi.groundwork.feature.sites.editor.SiteEditorRoute
import com.nabadi.groundwork.feature.sites.detail.SiteDetailRoute
import com.nabadi.groundwork.feature.sites.list.SitesListRoute

@Composable
fun GroundWorkNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = GroundWorkRoute.SITES_LIST,
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
                navArgument(GroundWorkRoute.SITE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            FieldNoteEditorRoute(
                onEditorFinished = {
                    navController.popBackStack()
                },
            )
        }

        composable(route = GroundWorkRoute.SITES_LIST) {
            SitesListRoute(
                onOpenSiteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.siteDetail(siteId))
                },
                onEditSiteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.siteEditor(siteId))
                },
                onAddSiteClick = {
                    navController.navigate(GroundWorkRoute.siteEditor())
                },
            )
        }

        composable(
            route = GroundWorkRoute.SITE_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(GroundWorkRoute.SITE_ID_ARG) {
                    type = NavType.StringType
                },
            ),
        ) {
            SiteDetailRoute(
                onBackClick = { navController.popBackStack() },
                onEditSiteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.siteEditor(siteId))
                },
                onFieldNoteClick = { fieldNoteId ->
                    navController.navigate(GroundWorkRoute.fieldNoteEditor(fieldNoteId))
                },
                onAddFieldNoteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.fieldNoteEditor(siteId = siteId))
                },
            )
        }

        composable(
            route = GroundWorkRoute.SITE_EDITOR_ROUTE,
            arguments = listOf(
                navArgument(GroundWorkRoute.SITE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            SiteEditorRoute(
                onEditorFinished = {
                    navController.popBackStack()
                },
            )
        }
    }
}
