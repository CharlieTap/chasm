package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertInvalidCommand

typealias AssertInvalidCommandRunner = (ScriptContext, AssertInvalidCommand) -> CommandResult

fun AssertInvalidCommandRunner(
    context: ScriptContext,
    command: AssertInvalidCommand,
): CommandResult {
    val moduleFilename = command.binaryFilename ?: command.filename
    val moduleFilePath = context.binaryDirectory + "/" + moduleFilename
    val bytes = moduleFilePath.readBytesFromPath()

    return module(bytes, context.config.moduleConfig)
        .flatMap { module ->
            validate(module)
        }.fold({ _ ->
            CommandResult.Failure(command, "invalid module was validated when it should have failed")
        }) {
            CommandResult.Success
        }
}
