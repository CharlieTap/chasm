package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.jit.instance
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

    return runBlocking { module(bytes, context.config.moduleConfig, validate = true) }
        .flatMap { module ->
            runBlocking { instance(context.store, module, context.imports, context.config.runtimeConfig) }
        }.fold({
            CommandResult.Failure(command, "instantiation succeeded when it shouldn't")
        }) {
            CommandResult.Success
        }
}
