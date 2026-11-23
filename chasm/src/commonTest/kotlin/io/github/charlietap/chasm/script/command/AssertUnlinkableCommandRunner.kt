package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUnlinkableCommand

typealias AssertUnlinkableCommandRunner = suspend (ScriptContext, AssertUnlinkableCommand) -> CommandResult

suspend fun AssertUnlinkableCommandRunner(
    context: ScriptContext,
    command: AssertUnlinkableCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    val module = when (val decoded = module(bytes, context.moduleConfig)) {
        is ChasmResult.Success -> decoded.result
        is ChasmResult.Error -> return CommandResult.Success
    }

    return when (instance(context.store, module, context.imports)) {
        is ChasmResult.Success -> CommandResult.Failure(command, "unlinkable module was instantiated when it should have failed")
        is ChasmResult.Error -> CommandResult.Success
    }
}
