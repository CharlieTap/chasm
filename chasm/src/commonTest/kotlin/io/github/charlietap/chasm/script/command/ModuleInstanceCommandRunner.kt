package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.sweet.lib.command.ModuleInstanceCommand

typealias ModuleInstanceCommandRunner = (ScriptContext, ModuleInstanceCommand) -> CommandResult

fun ModuleInstanceCommandRunner(
    context: ScriptContext,
    command: ModuleInstanceCommand,
): CommandResult {

    val module = context.modules[command.module] ?: return CommandResult.Failure(command, "Failed to lookup module in context")
    val result = instance(context.store, module, context.imports, context.config.runtimeConfig)

    return result.fold(
        { instance ->
            context.instances[null] = instance
            context.instances[command.instance] = instance
            CommandResult.Success
        },
    ) {
        CommandResult.Failure(command, "Failed to instantiate module: $it")
    }
}
