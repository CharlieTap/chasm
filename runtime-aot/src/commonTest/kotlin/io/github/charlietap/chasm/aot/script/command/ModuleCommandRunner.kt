package io.github.charlietap.chasm.aot.script.command

import io.github.charlietap.chasm.aot.script.ScriptContext
import io.github.charlietap.chasm.aot.script.decoder.BinaryDecoder
import io.github.charlietap.chasm.aot.script.decoder.BinaryValidator
import io.github.charlietap.chasm.aot.script.ext.readBytesFromPath
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.lib.command.ModuleCommand

typealias ModuleCommandRunner = (ScriptContext, ModuleCommand) -> CommandResult

fun ModuleCommandRunner(
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
        SemanticPhase.EXECUTION -> context.instantiate(bytes).map { instance ->
            context.instances[null] = instance
            command.name?.let {
                context.instances[command.name] = instance
            }
        }
    }

    return result.fold(
        { CommandResult.Success },
    ) {
        CommandResult.Failure(command, "Failed to instantiate module: $it")
    }
}
