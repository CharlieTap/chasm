package io.github.charlietap.chasm.compiled.script.command

import io.github.charlietap.chasm.compiled.script.ScriptContext
import io.github.charlietap.chasm.compiled.script.decoder.BinaryDecoder
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.sweet.lib.command.AssertMalformedCommand

typealias AssertMalformedCommandRunner = (ScriptContext, AssertMalformedCommand) -> CommandResult

fun AssertMalformedCommandRunner(
    context: ScriptContext,
    command: AssertMalformedCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = context.files(moduleFilePath)

    return BinaryDecoder(bytes, context.config.moduleConfig).fold({
        CommandResult.Failure(command, "malformed module was decoded when it should have failed")
    }) {
        CommandResult.Success
    }
}
