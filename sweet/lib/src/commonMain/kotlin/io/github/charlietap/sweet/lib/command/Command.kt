package io.github.charlietap.sweet.lib.command

import kotlinx.serialization.Serializable

@Serializable
sealed interface Command {
    abstract val line: Int
}
