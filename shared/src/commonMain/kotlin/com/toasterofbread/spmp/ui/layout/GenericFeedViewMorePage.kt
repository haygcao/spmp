package com.toasterofbread.spmp.ui.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toasterofbread.spmp.api.getGenericFeedViewMorePage
import com.toasterofbread.spmp.model.Settings
import com.toasterofbread.spmp.model.mediaitem.MediaItem
import com.toasterofbread.spmp.model.mediaitem.MediaItemPreviewParams
import com.toasterofbread.spmp.ui.component.MusicTopBar
import com.toasterofbread.spmp.ui.component.getDefaultMediaItemPreviewSize
import com.toasterofbread.spmp.ui.component.multiselect.MediaItemMultiSelectContext
import com.toasterofbread.utils.composable.SubtleLoadingIndicator

@Composable
fun GenericFeedViewMorePage(browse_id: String, modifier: Modifier = Modifier, bottom_padding: Dp = 0.dp) {
    var items_result: Result<List<MediaItem>>? by remember { mutableStateOf(null) }
    LaunchedEffect(browse_id) {
        items_result = null
        items_result = getGenericFeedViewMorePage(browse_id)
    }

    Column(modifier) {
        MusicTopBar(
            Settings.KEY_LYRICS_SHOW_IN_VIEWMORE,
            Modifier.fillMaxWidth()
        )

        items_result?.fold(
            { items ->
                val multiselect_context = remember { MediaItemMultiSelectContext() }

                val item_size = getDefaultMediaItemPreviewSize()
                val item_spacing = 20.dp
                val item_arrangement = Arrangement.spacedBy(item_spacing)

                Column(Modifier.fillMaxSize().padding(horizontal = item_spacing)) {
                    AnimatedVisibility(multiselect_context.is_active) {
                        multiselect_context.InfoDisplay(Modifier.fillMaxWidth().padding(top = item_spacing))
                    }

                    LazyVerticalGrid(
                        GridCells.Adaptive(item_size.width),
                        Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            top = item_spacing,
                            bottom = bottom_padding
                        ),
                        verticalArrangement = item_arrangement,
                        horizontalArrangement = item_arrangement
                    ) {
                        items(items) { item ->
                            item.PreviewSquare(MediaItemPreviewParams(
                                Modifier.size(item_size),
                                multiselect_context = multiselect_context
                            ))
                        }
                    }
                }

            },
            { error ->
                // TODO
                Text(error.stackTraceToString())
            }
        ) ?: Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
            SubtleLoadingIndicator()
        }
    }
}