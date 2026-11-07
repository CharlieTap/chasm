package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
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

    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            CommandResult.Failure(command, "expected trap ${command.text} but action succeeded")
        }
        is ActionResult.Failure -> {

            if (result.error is ChasmError.ExecutionError) {
                CommandResult.Success
            } else {
                CommandResult.Failure(command, "expected trap but encountered error ${result.error} with reason ${result.reason}")
            }
        }
    }
}
