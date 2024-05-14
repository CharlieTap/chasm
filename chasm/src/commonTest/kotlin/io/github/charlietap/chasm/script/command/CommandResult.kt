package io.github.charlietap.chasm.script.command

import io.github.charlietap.sweet.lib.command.Command

sealed interface CommandResult {

    data object Success : CommandResult

    data class Failure(
        val command: Command,
        val reason: String,
    ) : CommandResult
}
