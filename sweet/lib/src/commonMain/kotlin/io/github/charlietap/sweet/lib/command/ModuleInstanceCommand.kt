package io.github.charlietap.sweet.lib.command

import io.github.charlietap.sweet.lib.ModuleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("module_instance")
data class ModuleInstanceCommand(
    override val line: Int,
    val module: String,
    val instance: String,
): Command
