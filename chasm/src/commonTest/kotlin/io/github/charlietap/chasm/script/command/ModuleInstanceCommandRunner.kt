package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ScriptInstance
import io.github.charlietap.chasm.script.ScriptModule
import io.github.charlietap.sweet.lib.command.ModuleInstanceCommand

typealias ModuleInstanceCommandRunner = (ScriptContext, ModuleInstanceCommand) -> CommandResult

fun ModuleInstanceCommandRunner(
    context: ScriptContext,
    command: ModuleInstanceCommand,
): CommandResult {

    val module = context.modules[command.module] ?: return CommandResult.Failure(command, "Failed to lookup module in context")
    val result = when (module) {
        is ScriptModule.Core -> instance(
            context.store,
            module.module,
            context.imports,
            context.config.runtimeConfig,
        ).let { coreResult ->
            coreResult.fold(
                onSuccess = { instance -> ScriptInstance.Core(instance) to null },
                onError = { error -> null to error },
            )
        }
        is ScriptModule.ComponentModel -> instance(
            context.store,
            module.component,
            context.componentImports(module.component),
            context.config.runtimeConfig,
        ).let { componentResult ->
            componentResult.fold(
                onSuccess = { instance -> ScriptInstance.ComponentModel(instance) to null },
                onError = { error -> null to error },
            )
        }
    }

    val instance = result.first ?: return CommandResult.Failure(
        command,
        "Failed to instantiate module: ${result.second}",
    )
    context.instances[null] = instance
    context.instances[command.instance] = instance
    return CommandResult.Success
}
