package io.github.charlietap.sweet.lib

import io.github.charlietap.sweet.lib.command.Command
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Script(
    @SerialName("source_filename")
    val filename: String,
    val commands: List<Command>,
)
