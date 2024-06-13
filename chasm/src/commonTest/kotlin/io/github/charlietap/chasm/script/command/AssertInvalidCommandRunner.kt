package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.flatMap
import io.github.charlietap.chasm.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertInvalidCommand

typealias AssertInvalidCommandRunner = (ScriptContext, AssertInvalidCommand) -> CommandResult

fun AssertInvalidCommandRunner(
    context: ScriptContext,
    command: AssertInvalidCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    return module(bytes).flatMap { module ->
        validate(module)
    }.fold({ _ ->
        CommandResult.Failure(command, "invalid module was decoded when it should have failed")
    }) {
        CommandResult.Success
    }
}
