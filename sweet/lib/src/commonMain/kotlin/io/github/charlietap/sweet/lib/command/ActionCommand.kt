package io.github.charlietap.sweet.lib.command

import io.github.charlietap.sweet.lib.action.Action
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("action")
data class ActionCommand(
    override val line: Int,
    val action: Action,
): Command
