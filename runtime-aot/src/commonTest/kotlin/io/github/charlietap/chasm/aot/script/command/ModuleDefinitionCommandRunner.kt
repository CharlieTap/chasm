package io.github.charlietap.chasm.aot.script.command

import io.github.charlietap.chasm.aot.script.ScriptContext
import io.github.charlietap.chasm.aot.script.decoder.BinaryDecoder
import io.github.charlietap.chasm.aot.script.decoder.BinaryValidator
import io.github.charlietap.chasm.aot.script.ext.readBytesFromPath
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.shapes.map
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
        SemanticPhase.DECODING -> return BinaryDecoder(bytes, context.config.moduleConfig).fold(
            { CommandResult.Success },
        ) { CommandResult.Failure(command, "Failed to decode module definition: $it") }
        SemanticPhase.VALIDATION -> BinaryValidator(bytes, context.config.moduleConfig)
        SemanticPhase.EXECUTION -> context.compile(bytes).map { module ->
            context.modules[null] = module
            command.name?.let {
                context.modules[command.name] = module
            }
        }
    }

    return result.fold(
        { CommandResult.Success },
    ) {
        CommandResult.Failure(command, "Failed to instantiate module: $it")
    }
}
