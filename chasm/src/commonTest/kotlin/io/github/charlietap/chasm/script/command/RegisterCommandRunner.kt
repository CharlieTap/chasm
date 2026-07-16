package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.sweet.lib.command.RegisterCommand

typealias RegisterCommandRunner = (ScriptContext, RegisterCommand) -> CommandResult

fun RegisterCommandRunner(
    context: ScriptContext,
    command: RegisterCommand,
): CommandResult {

    val instance = context.instance(command.name)

    if (!context.registerImports(command.registerAs, instance)) {
        return CommandResult.Failure(command, "component instance registration is not executable yet")
    }

    return CommandResult.Success
}
