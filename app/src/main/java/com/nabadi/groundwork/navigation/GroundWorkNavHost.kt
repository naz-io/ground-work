package com.nabadi.groundwork.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
        startDestination = GroundWorkRoute.SitesList,
        modifier = modifier,
    ) {
        composable<GroundWorkRoute.FieldNotesList> {
            FieldNotesListRoute(
                onFieldNoteClick = { fieldNoteId ->
                    navController.navigate(GroundWorkRoute.FieldNoteEditor(fieldNoteId = fieldNoteId.value))
                },
                onAddFieldNoteClick = {
                    navController.navigate(GroundWorkRoute.FieldNoteEditor())
                },
            )
        }

        composable<GroundWorkRoute.FieldNoteEditor> { backStackEntry ->
            FieldNoteEditorRoute(
                route = backStackEntry.toRoute(),
                onEditorFinished = {
                    navController.popBackStack()
                },
            )
        }

        composable<GroundWorkRoute.SitesList> {
            SitesListRoute(
                onOpenSiteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.SiteDetail(siteId = siteId.value))
                },
                onAddSiteClick = {
                    navController.navigate(GroundWorkRoute.SiteEditor())
                },
            )
        }

        composable<GroundWorkRoute.SiteDetail> { backStackEntry ->
            SiteDetailRoute(
                route = backStackEntry.toRoute(),
                onBackClick = { navController.popBackStack() },
                onEditSiteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.SiteEditor(siteId = siteId.value))
                },
                onFieldNoteClick = { fieldNoteId ->
                    navController.navigate(GroundWorkRoute.FieldNoteEditor(fieldNoteId = fieldNoteId.value))
                },
                onAddFieldNoteClick = { siteId ->
                    navController.navigate(GroundWorkRoute.FieldNoteEditor(siteId = siteId.value))
                },
            )
        }

        composable<GroundWorkRoute.SiteEditor> { backStackEntry ->
            SiteEditorRoute(
                route = backStackEntry.toRoute(),
                onEditorFinished = {
                    navController.popBackStack()
                },
                onSiteDeleted = {
                    navController.popBackToSitesList()
                },
            )
        }
    }
}
