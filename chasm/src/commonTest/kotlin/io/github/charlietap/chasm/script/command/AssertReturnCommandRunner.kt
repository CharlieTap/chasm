package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.chasm.script.ext.mismatchTemplate
import io.github.charlietap.chasm.script.value.ValueMapper
import io.github.charlietap.sweet.lib.command.AssertReturnCommand

typealias AssertReturnCommandRunner = (ScriptContext, AssertReturnCommand) -> CommandResult

fun AssertReturnCommandRunner(
    context: ScriptContext,
    command: AssertReturnCommand,
): CommandResult = AssertReturnCommandRunner(
    context,
    command,
    ::ActionRunner,
    ::ValueMapper,
)

fun AssertReturnCommandRunner(
    context: ScriptContext,
    command: AssertReturnCommand,
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
