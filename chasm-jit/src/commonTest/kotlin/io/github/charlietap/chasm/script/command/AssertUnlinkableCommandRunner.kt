package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.jit.instance
import io.github.charlietap.chasm.jit.module
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUnlinkableCommand
import kotlinx.coroutines.runBlocking

typealias AssertUnlinkableCommandRunner = (ScriptContext, AssertUnlinkableCommand) -> CommandResult

fun AssertUnlinkableCommandRunner(
    context: ScriptContext,
    command: AssertUnlinkableCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    return runBlocking { module(bytes, context.config.moduleConfig, validate = true) }
        .flatMap { module ->
            instance(context.store, module, context.imports, context.config.runtimeConfig)
        }.fold({ _ ->
            CommandResult.Failure(command, "unlinkable module was instantiated when it should have failed")
        }) {
            CommandResult.Success
        }
}
