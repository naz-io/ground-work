package com.nabadi.groundwork.feature.sites.list

import com.nabadi.groundwork.domain.model.SitePriority
import org.junit.Assert.assertEquals
import org.junit.Test

class SitesSearchAndFiltersTest {

    @Test
    fun `priority filters follow the design order`() {
        assertEquals(
            listOf(
                SitePriority.URGENT,
                SitePriority.HIGH,
                SitePriority.NORMAL,
                SitePriority.LOW,
            ),
            sitePriorityFilterOrder,
        )
    }
}
