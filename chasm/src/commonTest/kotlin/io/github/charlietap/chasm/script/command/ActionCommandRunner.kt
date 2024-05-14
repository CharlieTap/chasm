package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.chasm.script.ext.mismatchTemplate
import io.github.charlietap.chasm.script.value.ValueMapper
import io.github.charlietap.sweet.lib.command.ActionCommand

typealias ActionCommandRunner = (ScriptContext, ActionCommand) -> CommandResult

fun ActionCommandRunner(
    context: ScriptContext,
    command: ActionCommand,
): CommandResult = ActionCommandRunner(
    context = context,
    command = command,
    actionRunner = ::ActionRunner,
    valueMapper = ::ValueMapper,
)

private fun ActionCommandRunner(
    context: ScriptContext,
    command: ActionCommand,
    actionRunner: ActionRunner,
    valueMapper: ValueMapper,
): CommandResult {
    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            val expected = command.expected.map(valueMapper)
            if (result.value == expected) {
                CommandResult.Success
            } else {
                val mismatch = mismatchTemplate(expected, result.value)
                CommandResult.Failure(command, mismatch)
            }
        }
        is ActionResult.Failure -> {
            CommandResult.Failure(command, result.reason)
        }
    }
}
