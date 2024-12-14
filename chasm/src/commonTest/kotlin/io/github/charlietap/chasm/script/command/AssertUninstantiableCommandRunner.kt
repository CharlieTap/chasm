package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.AssertUninstantiableCommand

typealias AssertUninstantiableCommandRunner = (ScriptContext, AssertUninstantiableCommand) -> CommandResult

fun AssertUninstantiableCommandRunner(
    context: ScriptContext,
    command: AssertUninstantiableCommand,
): CommandResult {

    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()

    return module(bytes)
        .flatMap { module ->
            validate(module)
        }.flatMap { module ->
            instance(context.store, module, context.imports)
        }.fold({
            CommandResult.Failure(command, "instantiation succeeded when it shouldn't")
        }) {
            CommandResult.Success
        }
}
