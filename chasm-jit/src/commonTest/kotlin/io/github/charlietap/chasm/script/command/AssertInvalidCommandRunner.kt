package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.decoder.BinaryValidator
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

    return BinaryValidator(bytes, context.config.moduleConfig).fold({ _ ->
        CommandResult.Failure(command, "invalid module was validated when it should have failed")
    }) {
        CommandResult.Success
    }
}
