package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertMalformedCommand

typealias AssertMalformedCommandRunner = (ScriptContext, AssertMalformedCommand) -> CommandResult

fun AssertMalformedCommandRunner(
    context: ScriptContext,
    command: AssertMalformedCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    return module(bytes).fold({
        CommandResult.Failure(command, "malformed module was decoded when it should have failed")
    }) {
        CommandResult.Success
    }
}
