package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.fold
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
    val arguments = action.args.map(valueMapper)
    if (arguments.any { argument -> argument == null }) {
        return ActionResult.Failure(command, "an argument of ${action.field} cannot be expressed on a compiled store: ${action.args}")
    }

    val result = invoke(
        context.instances[action.moduleName]!!,
        action.field,
        arguments.filterNotNull(),
    )

    return result.fold({ results ->
        ActionResult.Success(results)
    }) { error ->
        ActionResult.Failure(command, "invoke action returned an error", error)
    }
}
