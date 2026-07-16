package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.embedding.component
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.decoder.WasmLayer
import io.github.charlietap.chasm.script.decoder.WasmLayerDecoder
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUnlinkableCommand

typealias AssertUnlinkableCommandRunner = (ScriptContext, AssertUnlinkableCommand) -> CommandResult

fun AssertUnlinkableCommandRunner(
    context: ScriptContext,
    command: AssertUnlinkableCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    val linked = when (WasmLayerDecoder(bytes)) {
        WasmLayer.Core -> module(bytes, context.config.moduleConfig)
            .flatMap(::validate)
            .flatMap { module ->
                instance(context.store, module, context.imports, context.config.runtimeConfig)
            }.fold({ true }) { false }
        WasmLayer.Component -> component(bytes, ComponentConfig(context.config.moduleConfig))
            .flatMap(::validate)
            .flatMap { component ->
                instance(
                    context.store,
                    component,
                    context.componentImports(component),
                    context.config.runtimeConfig,
                )
            }.fold({ true }) { false }
        null -> false
    }

    return if (linked) {
        CommandResult.Failure(command, "unlinkable module was instantiated when it should have failed")
    } else {
        CommandResult.Success
    }
}
