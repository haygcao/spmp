package com.toasterofbread.spmp.ui.layout.apppage.searchpage

import LocalPlayerState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toasterofbread.spmp.service.playercontroller.PlayerState
import com.toasterofbread.spmp.ui.component.LargeFilterList
import com.toasterofbread.spmp.ui.layout.contentbar.LayoutSlot
import com.toasterofbread.spmp.youtubeapi.endpoint.*
import com.toasterofbread.composekit.utils.modifier.horizontal
import com.toasterofbread.composekit.utils.modifier.vertical
import com.toasterofbread.composekit.utils.common.copy

@Composable
internal fun SearchAppPage.VerticalSearchPrimaryBar(
    slot: LayoutSlot,
    modifier: Modifier,
    content_padding: PaddingValues
) {
    val player: PlayerState = LocalPlayerState.current

    LargeFilterList(
        SearchType.entries.size + 1,
        getItemText = { index ->
            val search_type: SearchType? = if (index == 0) null else SearchType.entries[index - 1]
            return@LargeFilterList search_type.getReadable()
        },
        getItemIcon = { index ->
            val search_type: SearchType? = if (index == 0) null else SearchType.entries[index - 1]
            return@LargeFilterList search_type.getIcon()
        },
        isItemSelected = { index ->
            if (current_filter == null) index == 0
            else current_filter!!.ordinal == index - 1
        },
        modifier = modifier.width(125.dp),
        content_padding = content_padding.copy(start = 0.dp),
        onSelected = { index ->
            if (index == 0) {
                setFilter(null)
            }
            else {
                setFilter(SearchType.entries[index - 1])
            }
        }
    )
}
