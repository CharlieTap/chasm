package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.sweet.lib.command.AssertTrapCommand

typealias AssertTrapCommandRunner = (ScriptContext, AssertTrapCommand) -> CommandResult

fun AssertTrapCommandRunner(
    context: ScriptContext,
    command: AssertTrapCommand,
): CommandResult = AssertTrapCommandRunner(
    context = context,
    command = command,
    actionRunner = ::ActionRunner,
)

private fun AssertTrapCommandRunner(
    context: ScriptContext,
    command: AssertTrapCommand,
    actionRunner: ActionRunner,
): CommandResult {
    println("ignoring AssertTrapCommand")
    return CommandResult.Success
}
