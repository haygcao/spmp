package com.toasterofbread.spmp.ui.shortcut.trigger

import kotlinx.serialization.Serializable
import com.toasterofbread.spmp.ui.shortcut.ShortcutState
import com.toasterofbread.spmp.ui.shortcut.LocalShortcutState
import com.toasterofbread.spmp.resources.getString
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement

@Serializable
data class KeyboardShortcutTrigger(
    val key_code: Long? = null,
    val modifiers: List<KeyboardModifier> = emptyList()
): ShortcutTrigger {
    private val key: Key? get() = key_code?.let { Key(it) }

    override fun getType(): ShortcutTrigger.Type =
        ShortcutTrigger.Type.KEYBOARD_SHORTCUT

    @Composable
    override fun IndicatorContent(modifier: Modifier) {
        Text(key.toString())
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun ConfigurationItems(item_modifier: Modifier, onModification: (ShortcutTrigger) -> Unit) {
        var detecting_key: Boolean by remember { mutableStateOf(false) }
        val shortcut_state: ShortcutState = LocalShortcutState.current

        DisposableEffect(detecting_key) {
            if (detecting_key) {
                shortcut_state.setKeyDetectionState { key ->
                    if (KeyboardModifier.ofKey(key) != null) {
                        return@setKeyDetectionState
                    }

                    onModification(copy(key_code = key.keyCode))
                    detecting_key = false
                }
            }

            onDispose {
                shortcut_state.setKeyDetectionState(null)
            }
        }

        FlowRow(
            item_modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                getString("shortcut_key_config_selected_key"),
                Modifier.align(Alignment.CenterVertically)
            )

            Button({
                detecting_key = !detecting_key
            }) {
                if (detecting_key) {
                    Text(getString("shortcut_key_config_detecting_key"))
                }
                else {
                    Text(key?.getName() ?: getString("shortcut_key_config_none_selected"))
                }
            }
        }

        for (modifier in KeyboardModifier.entries) {
            FlowRow(
                item_modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier.getName(),
                    Modifier.align(Alignment.CenterVertically)
                )

                Switch(
                    checked = modifiers.contains(modifier),
                    onCheckedChange = { checked ->
                        if (checked) {
                            onModification(copy(modifiers = modifiers.plus(modifier)))
                        }
                        else {
                            onModification(copy(modifiers = modifiers.minus(modifier)))
                        }
                    }
                )
            }
        }
    }

    enum class KeyboardModifier {
        CTRL, ALT, SHIFT;

        fun getName(): String =
            when (this) {
                CTRL -> getString("shortcut_key_modifier_ctrl")
                ALT -> getString("shortcut_key_modifier_alt")
                SHIFT -> getString("shortcut_key_modifier_shift")
            }

        companion object {
            fun ofKey(key: Key): KeyboardModifier? =
                when (key) {
                    Key.CtrlLeft, Key.CtrlRight -> CTRL
                    Key.AltLeft, Key.AltRight -> ALT
                    Key.ShiftLeft, Key.ShiftRight -> SHIFT
                    else -> null
                }
        }
    }
}

private fun Key.getName(): String {
    val name: String = toString().removePrefix("Key: ")
    return when (name) {
        "Windows" -> "Super"
        else -> name
    }
}
