package io.github.charlietap.chasm.script.command

import io.github.charlietap.sweet.lib.command.AssertUnlinkableCommand

typealias AssertUnlinkableCommandRunner = (AssertUnlinkableCommand) -> CommandResult

@Suppress("UNUSED_PARAMETER")
fun AssertUnlinkableCommandRunner(
    command: AssertUnlinkableCommand,
): CommandResult {
    println("ignoring AssertUnlinkableCommand")
    return CommandResult.Success
}
