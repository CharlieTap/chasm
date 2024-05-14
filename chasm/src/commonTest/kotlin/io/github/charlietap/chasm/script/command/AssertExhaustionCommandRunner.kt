package io.github.charlietap.chasm.script.command

import io.github.charlietap.sweet.lib.command.AssertExhaustionCommand

typealias AssertExhaustionCommandRunner = (AssertExhaustionCommand) -> CommandResult

@Suppress("UNUSED_PARAMETER")
fun AssertExhaustionCommandRunner(
    command: AssertExhaustionCommand,
): CommandResult {
    println("ignoring AssertExhaustionCommand")
    return CommandResult.Success
}
