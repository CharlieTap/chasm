package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.sweet.lib.command.AssertExhaustionCommand

typealias AssertExhaustionCommandRunner = (ScriptContext, AssertExhaustionCommand) -> CommandResult

fun AssertExhaustionCommandRunner(
    context: ScriptContext,
    command: AssertExhaustionCommand,
): CommandResult = AssertExhaustionCommandRunner(
    context = context,
    command = command,
    actionRunner = ::ActionRunner,
)

fun AssertExhaustionCommandRunner(
    context: ScriptContext,
    command: AssertExhaustionCommand,
    actionRunner: ActionRunner,
): CommandResult {
    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            CommandResult.Failure(command, "exhaustion command succeeded when it should have failed")
        }
        is ActionResult.Failure -> {
            val error = result.error
            if (error is ChasmError.ExecutionError && error.error is InvocationError.CallStackExhausted) {
                CommandResult.Success
            } else {
                CommandResult.Failure(command, result.reason)
            }
        }
    }
}
