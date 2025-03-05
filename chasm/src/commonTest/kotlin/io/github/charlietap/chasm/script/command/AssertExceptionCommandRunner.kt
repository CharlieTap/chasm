package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.sweet.lib.command.AssertExceptionCommand

typealias AssertExceptionCommandRunner = (ScriptContext, AssertExceptionCommand) -> CommandResult

fun AssertExceptionCommandRunner(
    context: ScriptContext,
    command: AssertExceptionCommand,
): CommandResult = AssertExceptionCommandRunner(
    context = context,
    command = command,
    actionRunner = ::ActionRunner,
)

private fun AssertExceptionCommandRunner(
    context: ScriptContext,
    command: AssertExceptionCommand,
    actionRunner: ActionRunner,
): CommandResult {

    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            CommandResult.Failure(command, "expected exception but action succeeded")
        }
        is ActionResult.Failure -> {
            if (result.error is ChasmError.ExecutionError && result.error.error == InvocationError.UncaughtException.toString()) {
                CommandResult.Success
            } else {
                CommandResult.Failure(command, "expected trap but encountered error ${result.error} with reason ${result.reason}")
            }
        }
    }
}
