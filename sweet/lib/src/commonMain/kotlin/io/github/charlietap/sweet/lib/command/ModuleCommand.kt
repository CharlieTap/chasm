package io.github.charlietap.sweet.lib.command

import io.github.charlietap.sweet.lib.ModuleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("module")
data class ModuleCommand(
    val filename: String,
    override val line: Int,
    val name: String? = null,
    @SerialName("module_type")
    val type: ModuleType? = null,
    @SerialName("binary_filename")
    val binaryFilename: String? = null,
): Command
