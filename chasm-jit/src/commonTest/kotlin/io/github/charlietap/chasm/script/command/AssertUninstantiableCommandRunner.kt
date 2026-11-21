package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.jit.module
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUninstantiableCommand
import kotlinx.coroutines.runBlocking

typealias AssertUninstantiableCommandRunner = (ScriptContext, AssertUninstantiableCommand) -> CommandResult

fun AssertUninstantiableCommandRunner(
    context: ScriptContext,
    command: AssertUninstantiableCommand,
): CommandResult {

    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    val module = when (val decoded = runBlocking { module(bytes, context.config.moduleConfig, validate = true) }) {
        is ChasmResult.Success -> decoded.result
        is ChasmResult.Error -> return CommandResult.Failure(command, "the uninstantiable module failed to load: ${decoded.error}")
    }

    return when (val instantiated = runBlocking { instance(context.store, module, context.imports, context.config.runtimeConfig) }) {
        is ChasmResult.Success -> CommandResult.Failure(command, "the uninstantiable module was instantiated")
        is ChasmResult.Error -> if (instantiated.error is ExecutionError.Trapped) {
            CommandResult.Success
        } else {
            CommandResult.Failure(command, "expected Trapped but instantiation failed with ${instantiated.error}")
        }
    }
}
