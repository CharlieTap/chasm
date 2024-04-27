package io.github.charlietap.chasm.script.command

import io.github.charlietap.sweet.lib.command.AssertInvalidCommand

typealias AssertInvalidCommandRunner = (AssertInvalidCommand) -> CommandResult

fun AssertInvalidCommandRunner(
    command: AssertInvalidCommand,
): CommandResult {
    println("ignoring AssertInvalidCommand")
    return CommandResult.Success
}
