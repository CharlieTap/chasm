package io.github.charlietap.chasm.script.command

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.flatMap
import io.github.charlietap.chasm.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.sweet.lib.command.AssertUninstantiableCommand

typealias AssertUninstantiableCommandRunner = (ScriptContext, AssertUninstantiableCommand) -> CommandResult

fun AssertUninstantiableCommandRunner(
    context: ScriptContext,
    command: AssertUninstantiableCommand,
): CommandResult {

    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = Resource(moduleFilePath).readBytes()

    return module(bytes).flatMap { module ->
        instance(context.store, module, context.imports)
    }.fold({
        CommandResult.Failure(command, "instantiation succeeded when it shouldn't")
    }) {
        CommandResult.Success
    }
}
