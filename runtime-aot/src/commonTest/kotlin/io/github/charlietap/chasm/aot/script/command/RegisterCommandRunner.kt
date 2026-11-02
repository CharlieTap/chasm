package io.github.charlietap.chasm.aot.script.command

import io.github.charlietap.chasm.aot.script.ScriptContext
import io.github.charlietap.sweet.lib.command.RegisterCommand

typealias RegisterCommandRunner = (ScriptContext, RegisterCommand) -> CommandResult

fun RegisterCommandRunner(
    context: ScriptContext,
    command: RegisterCommand,
): CommandResult {

    val instance = context.instance(command.name)

    context.registerImports(command.registerAs, instance)

    return CommandResult.Success
}
