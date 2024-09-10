package io.github.charlietap.sweet.lib.command

import io.github.charlietap.sweet.lib.action.Action
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("assert_trap")
data class AssertTrapCommand(
    override val line: Int,
    val action: Action,
    val text: String,
): Command
