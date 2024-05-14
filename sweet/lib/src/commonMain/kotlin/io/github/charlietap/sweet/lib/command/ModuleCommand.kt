package io.github.charlietap.sweet.lib.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("module")
data class ModuleCommand(
    val filename: String,
    override val line: Int,
    val name: String? = null,
): Command
