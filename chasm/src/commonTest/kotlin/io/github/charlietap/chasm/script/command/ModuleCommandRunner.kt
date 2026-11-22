package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.decoder.BinaryDecoder
import io.github.charlietap.chasm.script.decoder.BinaryValidator
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.lib.command.ModuleCommand

typealias ModuleCommandRunner = suspend (ScriptContext, ModuleCommand) -> CommandResult

suspend fun ModuleCommandRunner(
    context: ScriptContext,
    command: ModuleCommand,
): CommandResult {

    val moduleFilename = command.binaryFilename ?: command.filename
    val moduleFilePath = context.binaryDirectory + "/" + moduleFilename
    val bytes = moduleFilePath.readBytesFromPath()

    val result = when (context.phaseSupport) {
        SemanticPhase.DECODING -> return BinaryDecoder(bytes, context.config.moduleConfig).fold(
            { CommandResult.Success },
        ) { CommandResult.Failure(command, "Failed to decode module: $it") }

        SemanticPhase.VALIDATION -> BinaryValidator(bytes, context.config.moduleConfig)

        SemanticPhase.EXECUTION -> when (val decoded = module(bytes, context.config.moduleConfig)) {
            is ChasmResult.Success -> instance(context.store, decoded.result, context.imports, context.config.runtimeConfig)
                .map { instance ->
                    context.instances[null] = instance
                    command.name?.let {
                        context.instances[command.name] = instance
                    }
                }
            is ChasmResult.Error -> decoded
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
