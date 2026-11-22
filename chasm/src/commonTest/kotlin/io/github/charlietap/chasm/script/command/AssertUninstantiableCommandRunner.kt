package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUninstantiableCommand

typealias AssertUninstantiableCommandRunner = suspend (ScriptContext, AssertUninstantiableCommand) -> CommandResult

suspend fun AssertUninstantiableCommandRunner(
    context: ScriptContext,
    command: AssertUninstantiableCommand,
): CommandResult {

    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    val module = when (val decoded = module(bytes, context.config.moduleConfig)) {
        is ChasmResult.Success -> decoded.result
        is ChasmResult.Error -> return CommandResult.Success
    }

    return when (instance(context.store, module, context.imports, context.config.runtimeConfig)) {
        is ChasmResult.Success -> CommandResult.Failure(command, "instantiation succeeded when it shouldn't")
        is ChasmResult.Error -> CommandResult.Success
    }
}
