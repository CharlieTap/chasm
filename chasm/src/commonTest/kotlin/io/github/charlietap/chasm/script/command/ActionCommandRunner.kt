package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.sweet.lib.command.ActionCommand

typealias ActionCommandRunner = (ScriptContext, ActionCommand) -> CommandResult

fun ActionCommandRunner(
    context: ScriptContext,
    command: ActionCommand,
): CommandResult = ActionCommandRunner(
    context = context,
    command = command,
    actionRunner = ::ActionRunner,
)

private fun ActionCommandRunner(
    context: ScriptContext,
    command: ActionCommand,
    actionRunner: ActionRunner,
): CommandResult {
    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            CommandResult.Success
        }
        is ActionResult.Failure -> {
            CommandResult.Failure(command, result.reason)
        }
    }
}
