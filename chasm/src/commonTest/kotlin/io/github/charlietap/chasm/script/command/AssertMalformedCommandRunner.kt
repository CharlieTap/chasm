package io.github.charlietap.chasm.script.command

import io.github.charlietap.sweet.lib.command.AssertMalformedCommand

typealias AssertMalformedCommandRunner = (AssertMalformedCommand) -> CommandResult

@Suppress("UNUSED_PARAMETER")
fun AssertMalformedCommandRunner(
    command: AssertMalformedCommand,
): CommandResult {
    println("ignoring AssertMalformed")
    return CommandResult.Success
}
