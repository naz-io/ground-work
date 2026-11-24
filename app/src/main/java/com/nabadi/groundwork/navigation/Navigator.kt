package com.nabadi.groundwork.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events by updating [NavigationState].
 */
class Navigator(val state: NavigationState) {

    /** Switches tab for a top-level route; otherwise opens [route] on the current tab. */
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            currentStack.add(route)
        }
    }

    /** Closes the current screen; from another tab's root, returns to the start tab. */
    fun goBack() {
        if (currentStack.last() == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    /**
     * Closes every screen above [route] on the current tab, or just the current screen when
     * [route] isn't on this tab.
     */
    fun goBackTo(route: NavKey) {
        val stack = currentStack
        if (route !in stack) {
            goBack()
            return
        }
        while (stack.last() != route) {
            stack.removeLastOrNull()
        }
    }

    private val currentStack: NavBackStack<NavKey>
        get() = state.backStacks[state.topLevelRoute]
            ?: error("No back stack for ${state.topLevelRoute}")
}
