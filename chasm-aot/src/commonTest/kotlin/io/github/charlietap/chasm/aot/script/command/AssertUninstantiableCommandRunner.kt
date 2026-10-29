package io.github.charlietap.chasm.aot.script.command

import io.github.charlietap.chasm.aot.script.ScriptContext
import io.github.charlietap.chasm.aot.script.ext.readBytesFromPath
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.sweet.lib.command.AssertUninstantiableCommand

typealias AssertUninstantiableCommandRunner = (ScriptContext, AssertUninstantiableCommand) -> CommandResult

fun AssertUninstantiableCommandRunner(
    context: ScriptContext,
    command: AssertUninstantiableCommand,
): CommandResult {
    val moduleFilePath = context.binaryDirectory + "/" + command.filename
    val bytes = moduleFilePath.readBytesFromPath()
    val module = when (val compiled = context.compile(bytes)) {
        is ChasmResult.Success -> compiled.result
        is ChasmResult.Error -> return CommandResult.Failure(command, "module did not compile: ${compiled.error}")
    }
    return context.instantiate(module).fold({
        CommandResult.Failure(command, "instantiation succeeded when it shouldn't")
    }) {
        CommandResult.Success
    }
}
