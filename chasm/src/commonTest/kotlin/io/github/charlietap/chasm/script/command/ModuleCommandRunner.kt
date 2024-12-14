package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.validate
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.command.ModuleCommand

typealias ModuleCommandRunner = (ScriptContext, ModuleCommand) -> CommandResult

fun ModuleCommandRunner(
    context: ScriptContext,
    command: ModuleCommand,
): CommandResult {

    val moduleFilename = command.binaryFilename ?: command.filename
    val moduleFilePath = context.binaryDirectory + "/" + moduleFilename
    val bytes = moduleFilePath.readBytesFromPath()

    return module(bytes)
        .flatMap { module ->
            validate(module)
        }.flatMap { module ->
            instance(context.store, module, context.imports)
        }.fold({ instance ->
            context.instances[null] = instance
            command.name?.let {
                context.instances[command.name] = instance
            }
            CommandResult.Success
        }) {
            CommandResult.Failure(command, "Failed to instantiate module: $it")
        }
}
