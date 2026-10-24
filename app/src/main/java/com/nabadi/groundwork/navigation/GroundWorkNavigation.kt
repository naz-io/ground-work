package com.nabadi.groundwork.navigation

import androidx.navigation.NavController

// A deleted site's detail screen has nothing left to show, so skip past it.
internal fun NavController.popBackToSitesList() {
    if (!popBackStack<GroundWorkRoute.SitesList>(inclusive = false)) {
        popBackStack()
    }
}
