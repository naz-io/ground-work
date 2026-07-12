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
import com.nabadi.groundwork.R
import com.nabadi.groundwork.domain.model.FieldNoteStatus
import com.nabadi.groundwork.feature.fieldnotes.labelResId
import com.nabadi.groundwork.feature.fieldnotes.previewFieldNoteItems
import com.nabadi.groundwork.ui.components.GroundWorkPreviewSurface
import com.nabadi.groundwork.ui.components.GroundWorkShapes
import com.nabadi.groundwork.ui.components.PREVIEW_API_LEVEL
import com.nabadi.groundwork.ui.components.TechnicalLabel
import com.nabadi.groundwork.ui.format.absoluteDateTimeLabel
import androidx.compose.ui.platform.LocalLocale

@Composable
internal fun FieldNoteItemCard(
    fieldNoteItem: FieldNoteListItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val note = fieldNoteItem.note
    val locale = LocalLocale.current.platformLocale
    val statusColor = when (note.status) {
        FieldNoteStatus.ACTIVE -> MaterialTheme.colorScheme.primary
        FieldNoteStatus.DRAFT -> MaterialTheme.colorScheme.secondary
        FieldNoteStatus.ARCHIVED -> MaterialTheme.colorScheme.outline
    }
    val siteLabel = fieldNoteItem.siteName?.uppercase(locale)
        ?: stringResource(R.string.field_note_card_site_unassigned)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(
            dimensionResource(R.dimen.border_field_note_card),
            MaterialTheme.colorScheme.outlineVariant
        ),
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
                        vertical = dimensionResource(R.dimen.padding_field_note_card_header_vertical)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TechnicalLabel(
                    text = stringResource(
                        R.string.field_note_card_site_label,
                        siteLabel,
                    ),
                )
                FieldNoteStatusIcon(
                    status = note.status,
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
                    text = note.title.ifBlank { stringResource(R.string.field_notes_list_untitled_note_title) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = note.body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_field_note_card_footer_top)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TechnicalLabel(
                        text = stringResource(
                            R.string.field_note_card_updated_label,
                            note.updatedAt.absoluteDateTimeLabel().uppercase(locale),
                        ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_field_note_card_actions)),
                    ) {
                        if (note.siteId == null) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(R.dimen.size_field_note_card_action_icon)),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(R.dimen.size_field_note_card_action_icon)),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null,
                                modifier = Modifier.size(dimensionResource(R.dimen.size_field_note_card_action_icon)),
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
        modifier = modifier.size(dimensionResource(R.dimen.size_field_note_card_status_icon)),
        tint = tint,
    )
}

@Preview(
    name = "Field Note Card",
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteItemCardPreview() {
    GroundWorkPreviewSurface {
        FieldNoteItemCard(
            fieldNoteItem = previewFieldNoteItems.first(),
            onClick = {},
        )
    }
}

@Preview(
    name = "Field Note Card - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    apiLevel = PREVIEW_API_LEVEL,
)
@Composable
private fun FieldNoteItemCardPreview_Dark() {
    GroundWorkPreviewSurface {
        FieldNoteItemCard(
            fieldNoteItem = previewFieldNoteItems.first(),
            onClick = {},
        )
    }
}