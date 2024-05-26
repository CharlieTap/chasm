package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.value.ValueMapper
import io.github.charlietap.sweet.lib.action.InvokeAction
import io.github.charlietap.sweet.lib.command.Command

typealias InvokeActionRunner = (ScriptContext, Command, InvokeAction) -> ActionResult

fun InvokeActionRunner(
    context: ScriptContext,
    command: Command,
    action: InvokeAction,
) = InvokeActionRunner(
    context = context,
    command = command,
    action = action,
    valueMapper = ::ValueMapper,
)

private fun InvokeActionRunner(
    context: ScriptContext,
    command: Command,
    action: InvokeAction,
    valueMapper: ValueMapper,
): ActionResult {
    val result = invoke(
        context.store,
        context.instances[action.moduleName]!!,
        action.field,
        action.args.mapNotNull(valueMapper),
    )

    return result.fold({ results ->
        ActionResult.Success(results)
    }) { error ->
        ActionResult.Failure(command, "invoke action returned an error", error)
    }
}
