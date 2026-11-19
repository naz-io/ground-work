package com.nabadi.groundwork.ui.format

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class AbsoluteDateTimeFormatterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rememberedFormatter_usesCompositionLocaleAndSystemZone() {
        lateinit var formatter: DateTimeFormatter
        lateinit var compositionLocale: Locale

        composeTestRule.setContent {
            compositionLocale = LocalLocale.current.platformLocale
            formatter = rememberAbsoluteDateTimeFormatter()
        }

        composeTestRule.runOnIdle {
            assertEquals(compositionLocale, formatter.locale)
            assertEquals(ZoneId.systemDefault(), formatter.zone)
        }
    }

    @Test
    fun rememberedFormatter_isReusedAcrossRecompositions() {
        var recompositionTrigger by mutableIntStateOf(0)
        val formatters = mutableListOf<DateTimeFormatter>()

        composeTestRule.setContent {
            // Reading the trigger makes this scope recompose when it changes.
            recompositionTrigger
            formatters += rememberAbsoluteDateTimeFormatter()
        }

        composeTestRule.runOnIdle { recompositionTrigger++ }
        composeTestRule.runOnIdle { recompositionTrigger++ }

        composeTestRule.runOnIdle {
            assertEquals(3, formatters.size)
            formatters.forEach { assertSame(formatters.first(), it) }
        }
    }
}
