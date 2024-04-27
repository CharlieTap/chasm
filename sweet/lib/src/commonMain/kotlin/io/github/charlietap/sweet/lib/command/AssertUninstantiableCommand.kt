package io.github.charlietap.sweet.lib.command

import io.github.charlietap.sweet.lib.ModuleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("assert_uninstantiable")
data class AssertUninstantiableCommand(
    override val line: Int,
    val filename: String,
    val text: String,
    @SerialName("module_type")
    val moduleType: ModuleType,
): Command
