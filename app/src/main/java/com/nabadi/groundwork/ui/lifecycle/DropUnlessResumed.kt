package com.nabadi.groundwork.ui.lifecycle

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * The value-taking counterpart of `androidx.lifecycle.compose.dropUnlessResumed`,
 * for navigation callbacks such as `onOpenSiteClick(siteId)`.
 *
 * Navigating moves the current destination out of RESUMED at once, so a second
 * tap that lands during the transition is dropped instead of navigating twice.
 */
@Composable
fun <T> ((T) -> Unit).dropUnlessResumed(): (T) -> Unit {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val callback = this
    return { value ->
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            callback(value)
        }
    }
}
