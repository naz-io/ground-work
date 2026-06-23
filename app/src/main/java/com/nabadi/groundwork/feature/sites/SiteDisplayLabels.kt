package com.nabadi.groundwork.feature.sites

import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus

internal val SiteStatus.labelResId: Int
    get() = when (this) {
        SiteStatus.ACTIVE -> R.string.site_status_active
        SiteStatus.COMPLETED -> R.string.site_status_completed
        SiteStatus.ARCHIVED -> R.string.site_status_archived
    }

internal val SitePriority.labelResId: Int
    get() = when (this) {
        SitePriority.LOW -> R.string.site_priority_low
        SitePriority.NORMAL -> R.string.site_priority_normal
        SitePriority.HIGH -> R.string.site_priority_high
        SitePriority.URGENT -> R.string.site_priority_urgent
    }
