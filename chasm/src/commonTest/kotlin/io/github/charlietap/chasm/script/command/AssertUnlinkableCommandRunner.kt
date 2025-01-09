package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUnlinkableCommand

typealias AssertUnlinkableCommandRunner = (ScriptContext, AssertUnlinkableCommand) -> CommandResult

fun AssertUnlinkableCommandRunner(
    context: ScriptContext,
    command: AssertUnlinkableCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    return module(bytes, context.config.moduleConfig)
        .flatMap { module ->
            validate(module)
        }.flatMap { module ->
            instance(context.store, module, context.imports, context.config.runtimeConfig)
        }.fold({ _ ->
            CommandResult.Failure(command, "unlinkable module was instantiated when it should have failed")
        }) {
            CommandResult.Success
        }
}
