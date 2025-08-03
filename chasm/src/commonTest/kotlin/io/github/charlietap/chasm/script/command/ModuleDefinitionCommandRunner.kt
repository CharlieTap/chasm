package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.lib.command.ModuleDefinitionCommand

typealias ModuleDefinitionCommandRunner = (ScriptContext, ModuleDefinitionCommand) -> CommandResult

fun ModuleDefinitionCommandRunner(
    context: ScriptContext,
    command: ModuleDefinitionCommand,
): CommandResult {

    val moduleFilename = command.binaryFilename ?: command.filename
    val moduleFilePath = context.binaryDirectory + "/" + moduleFilename
    val bytes = moduleFilePath.readBytesFromPath()

    val result = when (context.phaseSupport) {
        SemanticPhase.DECODING -> module(bytes, context.config.moduleConfig)
        SemanticPhase.VALIDATION -> module(bytes, context.config.moduleConfig)
            .flatMap { module ->
                validate(module)
            }

        SemanticPhase.EXECUTION -> module(bytes, context.config.moduleConfig)
            .flatMap { module ->
                validate(module)
            }.map { module ->
                context.modules[null] = module
                command.name?.let {
                    context.modules[command.name] = module
                }
            }
    }

    return result.fold(
        { instance ->
            CommandResult.Success
        },
    ) {
        CommandResult.Failure(command, "Failed to instantiate module: $it")
    }
}
