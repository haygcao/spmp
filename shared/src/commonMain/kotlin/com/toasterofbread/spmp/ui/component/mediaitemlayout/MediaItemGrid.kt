package com.toasterofbread.spmp.ui.component.mediaitemlayout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.toasterofbread.spmp.model.mediaitem.MediaItemHolder
import com.toasterofbread.spmp.model.mediaitem.MediaItemPreviewParams
import com.toasterofbread.spmp.model.mediaitem.rememberFilteredItems
import com.toasterofbread.spmp.resources.uilocalisation.LocalisedYoutubeString
import com.toasterofbread.spmp.ui.component.mediaitempreview.MEDIA_ITEM_PREVIEW_LONG_HEIGHT
import com.toasterofbread.spmp.ui.component.multiselect.MediaItemMultiSelectContext
import com.toasterofbread.spmp.ui.theme.Theme
import com.toasterofbread.utils.modifier.background

@Composable
fun MediaItemGrid(
    layout: MediaItemLayout,
    modifier: Modifier = Modifier,
    rows: Int? = null,
    alt_style: Boolean = false,
    apply_filter: Boolean = false,
    multiselect_context: MediaItemMultiSelectContext? = null,
    startContent: (LazyGridScope.() -> Unit)? = null
) {
    MediaItemGrid(
        layout.items,
        modifier,
        rows,
        layout.title,
        layout.subtitle,
        layout.view_more,
        alt_style = alt_style,
        apply_filter = apply_filter,
        square_item_max_text_rows = layout.square_item_max_text_rows,
        itemSizeProvider = layout.itemSizeProvider,
        multiselect_context = multiselect_context,
        startContent = startContent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemGrid(
    items: List<MediaItemHolder>,
    modifier: Modifier = Modifier,
    rows: Int? = null,
    title: LocalisedYoutubeString? = null,
    subtitle: LocalisedYoutubeString? = null,
    view_more: MediaItemLayout.ViewMore? = null,
    alt_style: Boolean = false,
    square_item_max_text_rows: Int? = null,
    apply_filter: Boolean = false,
    itemSizeProvider: @Composable () -> DpSize = { getDefaultMediaItemPreviewSize() },
    multiselect_context: MediaItemMultiSelectContext? = null,
    startContent: (LazyGridScope.() -> Unit)? = null
) {
    val filtered_items = items.rememberFilteredItems(apply_filter)

    val row_count = (rows ?: if (filtered_items.size <= 3) 1 else 2) * (if (alt_style) 2 else 1)
    val item_spacing = Arrangement.spacedBy(if (alt_style) 7.dp else 15.dp)
    val item_size = if (alt_style) DpSize(0.dp, MEDIA_ITEM_PREVIEW_LONG_HEIGHT.dp) else itemSizeProvider()

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TitleBar(
            title,
            subtitle,
            view_more = view_more,
            multiselect_context = multiselect_context
        )

        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(row_count),
                modifier = Modifier
                    .height(item_size.height * row_count + item_spacing.spacing * (row_count - 1))
                    .fillMaxWidth(),
                horizontalArrangement = item_spacing,
                verticalArrangement = item_spacing
            ) {
                startContent?.invoke(this)

                items(filtered_items.size, { filtered_items[it].item?.id ?: "" }) { i ->
                    val item = filtered_items[i].item ?: return@items
                    val params = MediaItemPreviewParams(
                        Modifier.animateItemPlacement().then(
                            if (alt_style) Modifier.width(maxWidth * 0.9f)
                            else Modifier.size(item_size)
                        ),
                        contentColour = Theme.current.on_background_provider,
                        multiselect_context = multiselect_context,
                        square_item_max_text_rows = square_item_max_text_rows
                    )

                    if (alt_style) {
                        item.PreviewLong(params)
                    } else {
                        item.PreviewSquare(params)
                    }
                }
            }

            if (multiselect_context != null && !shouldShowTitleBar(title, subtitle)) {
                Box(Modifier.background(CircleShape, Theme.current.background_provider), contentAlignment = Alignment.Center) {
                    multiselect_context.CollectionToggleButton(filtered_items)
                }
            }
        }
    }
}