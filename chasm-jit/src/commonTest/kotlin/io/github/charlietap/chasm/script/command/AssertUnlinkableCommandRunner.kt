package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
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

    val module = when (val decoded = runBlocking { module(bytes, context.moduleConfig, validate = true) }) {
        is ChasmResult.Success -> decoded.result
        is ChasmResult.Error -> return CommandResult.Failure(command, "the unlinkable module failed to load: ${decoded.error}")
    }

    return when (val instantiated = runBlocking { instance(context.store, module, context.imports) }) {
        is ChasmResult.Success -> CommandResult.Failure(command, "the unlinkable module was instantiated")
        is ChasmResult.Error -> if (instantiated.error is ExecutionError.Unlinkable) {
            CommandResult.Success
        } else {
            CommandResult.Failure(command, "expected Unlinkable but instantiation failed with ${instantiated.error}")
        }
    }
}
