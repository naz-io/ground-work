package com.nabadi.groundwork.feature.sites

import com.nabadi.groundwork.domain.model.Site
import com.nabadi.groundwork.domain.model.SiteId
import com.nabadi.groundwork.domain.model.SitePriority
import com.nabadi.groundwork.domain.model.SiteStatus
import com.nabadi.groundwork.feature.sites.editor.SiteEditorUiState

internal const val PREVIEW_API_LEVEL = 35
internal const val previewNowMillis = 1_735_689_600_000L

internal val previewLongContentSite = Site(
    id = SiteId("SITE-LONG"),
    name = "North Warehouse Emergency Overflow Staging Area With Restricted Vehicle Access",
    description = "This site has recurring access issues during morning freight windows, damaged pallet racks near the west wall, temporary fencing around the exterior loading bay, and repeated safety notes from crews about poor lighting, blocked emergency exits, and unclear staging instructions during shift changes.",
    location = "Sector A · 125 Industrial Road · Rear service entrance beside loading docks 7 through 12",
    priority = SitePriority.URGENT,
    status = SiteStatus.ACTIVE,
    createdAt = 0,
    updatedAt = previewNowMillis - 45 * 60 * 1000,
)

internal val previewSites = listOf(
    Site(
        id = SiteId("SITE-001"),
        name = "North Warehouse",
        description = "Main storage facility with recurring loading-bay access issues and damaged pallet racks.",
        location = "Sector A · 125 Industrial Road",
        priority = SitePriority.HIGH,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 2 * 60 * 60 * 1000, // 2 hours ago
    ),
    Site(
        id = SiteId("SITE-002"),
        name = "South Terminal",
        description = "Loading dock inspection area for incoming freight and damaged trailer reports.",
        location = "Sector B · Dock 4",
        priority = SitePriority.NORMAL,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 24 * 60 * 60 * 1000, // 1 day ago
    ),
    Site(
        id = SiteId("SITE-003"),
        name = "Pine Creek Bridge",
        description = "Bridge maintenance zone with temporary fencing, drainage concerns, and weekly inspection notes.",
        location = "Pine Creek Road · Mile 18",
        priority = SitePriority.URGENT,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 3 * 24 * 60 * 60 * 1000, // 3 days ago
    ),
    Site(
        id = SiteId("SITE-004"),
        name = "Main Grid Transformer",
        description = "Electrical utility site with restricted access and required photo documentation after every visit.",
        location = "North Yard · Transformer 7",
        priority = SitePriority.HIGH,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 45 * 60 * 1000, // 45 min ago
    ),
    Site(
        id = SiteId("SITE-005"),
        name = "West Parking Structure",
        description = "Concrete repair and water ingress observations across levels P2 and P3.",
        location = "Civic Centre · West Lot",
        priority = SitePriority.NORMAL,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 6 * 24 * 60 * 60 * 1000, // 6 days ago
    ),
    Site(
        id = SiteId("SITE-006"),
        name = "River Pump Station",
        description = "Pump station with standing water reports, generator checks, and access-road erosion.",
        location = "Riverbend Service Road",
        priority = SitePriority.URGENT,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 12 * 60 * 60 * 1000, // 12 hours ago
    ),
    Site(
        id = SiteId("SITE-007"),
        name = "East Service Tunnel",
        description = "Tunnel inspection route with lighting issues and confined-space safety notes.",
        location = "Transit Yard · East Access",
        priority = SitePriority.HIGH,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 14 * 24 * 60 * 60 * 1000, // 14 days ago
    ),
    Site(
        id = SiteId("SITE-008"),
        name = "Admin Roof Access",
        description = "Roof inspection area with HVAC vibration reports and safety railing follow-up.",
        location = "Admin Building · Roof Level",
        priority = SitePriority.LOW,
        status = SiteStatus.COMPLETED,
        createdAt = 0,
        updatedAt = previewNowMillis - 30 * 24 * 60 * 60 * 1000, // 30 days ago
    ),
    Site(
        id = SiteId("SITE-009"),
        name = "Old Storage Annex",
        description = "Archived site kept for historical notes and completed remediation photos.",
        location = "Annex Road · Unit 12",
        priority = SitePriority.LOW,
        status = SiteStatus.ARCHIVED,
        createdAt = 0,
        updatedAt = previewNowMillis - 180 * 24 * 60 * 60 * 1000, // 180 days ago
    ),
    Site(
        id = SiteId("SITE-010"),
        name = "Fleet Maintenance Bay",
        description = "Vehicle service bay with spill reports, tool inventory notes, and repair photos.",
        location = "Operations Yard · Bay 3",
        priority = SitePriority.NORMAL,
        status = SiteStatus.ACTIVE,
        createdAt = 0,
        updatedAt = previewNowMillis - 5 * 60 * 1000, // 5 min ago
    ),
)

internal val emptySiteEditorPreviewState = SiteEditorUiState(
    name = "",
    location = "",
    description = "",
    isSaving = false,
    isEditing = false,
)

internal val filledSiteEditorPreviewState = SiteEditorUiState(
    name = "North Warehouse",
    location = "Sector A · 125 Industrial Road",
    description = "Main storage facility with recurring loading-bay access issues and damaged pallet racks.",
    isSaving = false,
    isEditing = false,
)

internal val savingSiteEditorPreviewState = SiteEditorUiState(
    name = "River Pump Station",
    location = "Riverbend Service Road",
    description = "Pump station with standing water reports, generator checks, and access-road erosion.",
    isSaving = true,
    isEditing = false,
)

internal val errorSiteEditorPreviewState = SiteEditorUiState(
    name = "East Service Tunnel",
    location = "Transit Yard · East Access",
    description = "Tunnel inspection route with lighting issues and confined-space safety notes.",
    isSaving = false,
    isEditing = false,
    errorMessage = "Unable to save site.",
)

internal val editingSiteEditorPreviewState = SiteEditorUiState(
    name = "Pine Creek Bridge",
    location = "Pine Creek Road · Mile 18",
    description = "Bridge maintenance zone with temporary fencing, drainage concerns, and weekly inspection notes.",
    isSaving = false,
    isEditing = true,
)

internal val longContentSiteEditorPreviewState = SiteEditorUiState(
    name = previewLongContentSite.name,
    location = previewLongContentSite.location,
    description = previewLongContentSite.description,
    priority = previewLongContentSite.priority,
    status = previewLongContentSite.status,
    isSaving = false,
    isEditing = true,
)

internal val deletingSiteEditorPreviewState = SiteEditorUiState(
    name = "Old Storage Annex",
    location = "Annex Road · Unit 12",
    description = "Archived site kept for historical notes and completed remediation photos.",
    priority = SitePriority.LOW,
    status = SiteStatus.ARCHIVED,
    isSaving = false,
    isDeleting = true,
    isEditing = true,
)
