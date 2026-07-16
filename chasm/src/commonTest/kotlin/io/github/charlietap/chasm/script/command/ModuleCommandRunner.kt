package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.embedding.component
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ScriptInstance
import io.github.charlietap.chasm.script.decoder.BinaryDecoder
import io.github.charlietap.chasm.script.decoder.BinaryValidator
import io.github.charlietap.chasm.script.decoder.WasmLayer
import io.github.charlietap.chasm.script.decoder.WasmLayerDecoder
import io.github.charlietap.chasm.script.ext.readBytesFromPath
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

        SemanticPhase.EXECUTION -> when (WasmLayerDecoder(bytes)) {
            WasmLayer.Core -> module(bytes, context.config.moduleConfig)
                .flatMap(::validate)
                .flatMap { module ->
                    instance(context.store, module, context.imports, context.config.runtimeConfig)
                }.map { instance ->
                    context.storeInstance(command.name, ScriptInstance.Core(instance))
                }
            WasmLayer.Component -> component(bytes, ComponentConfig(context.config.moduleConfig))
                .flatMap(::validate)
                .flatMap { component ->
                    instance(context.store, component, context.componentImports(component), context.config.runtimeConfig)
                }.map { instance ->
                    context.storeInstance(command.name, ScriptInstance.ComponentModel(instance))
                }
            null -> return CommandResult.Failure(command, "Failed to identify Wasm binary layer")
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

private fun ScriptContext.storeInstance(
    name: String?,
    instance: ScriptInstance,
) {
    instances[null] = instance
    name?.let { instances[it] = instance }
}
