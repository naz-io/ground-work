package com.nabadi.groundwork.feature.fieldnotes.list

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNote
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.feature.fieldnotes.PREVIEW_API_LEVEL
import com.nabadi.groundwork.feature.fieldnotes.labelResId
import com.nabadi.groundwork.feature.fieldnotes.previewFieldNotes
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes
import com.nabadi.groundwork.ui.components.TechnicalLabel
import com.nabadi.groundwork.ui.format.absoluteDateTimeLabel

@Composable
internal fun FieldNoteCard(
    fieldNote: FieldNote,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (fieldNote.status) {
        FieldNoteStatus.ACTIVE -> MaterialTheme.colorScheme.primary
        FieldNoteStatus.DRAFT -> MaterialTheme.colorScheme.secondary
        FieldNoteStatus.ARCHIVED -> MaterialTheme.colorScheme.outline
    }
    val siteLabel = fieldNote.siteId?.value
        ?.removePrefix("site-")
        ?.replace('-', ' ')
        ?.uppercase()
        ?: "UNASSIGNED"

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = GroundWorkShapes.Control,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_card_content),
                        vertical = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TechnicalLabel(text = "SITE: $siteLabel")
                FieldNoteStatusIcon(
                    status = fieldNote.status,
                    tint = statusColor,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(dimensionResource(R.dimen.padding_card_content)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_card_content)),
            ) {
                Text(
                    text = fieldNote.title.ifBlank { stringResource(R.string.field_notes_list_untitled_note_title) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = fieldNote.body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TechnicalLabel(
                        text = "UPDATED: ${
                            fieldNote.updatedAt.absoluteDateTimeLabel().uppercase()
                        }"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (fieldNote.siteId == null) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldNoteStatusIcon(
    status: FieldNoteStatus,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = when (status) {
            FieldNoteStatus.ACTIVE -> Icons.Filled.CheckCircle
            FieldNoteStatus.DRAFT -> Icons.Filled.Schedule
            FieldNoteStatus.ARCHIVED -> Icons.Filled.CheckCircle
        },
        contentDescription = stringResource(status.labelResId),
        modifier = modifier.size(16.dp),
        tint = tint,
    )
}

@Preview(
    name = "Field Note Card",
    showBackground = true,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteCardPreview() {
    GroundWorkPreviewSurface {
        FieldNoteCard(
            fieldNote = previewFieldNotes.first(),
            onClick = {},
        )
    }
}

@Preview(
    name = "Field Note Card - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteCardPreview_Dark() {
    GroundWorkPreviewSurface {
        FieldNoteCard(
            fieldNote = previewFieldNotes.first(),
            onClick = {},
        )
    }
}