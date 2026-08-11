package io.github.charlietap.sweet.lib

import io.github.charlietap.sweet.lib.command.Command

sealed interface ScriptResult {

    data object Success : ScriptResult

    data class Failure(
        val command: Command,
        val reason: String,
    ) : ScriptResult
}
