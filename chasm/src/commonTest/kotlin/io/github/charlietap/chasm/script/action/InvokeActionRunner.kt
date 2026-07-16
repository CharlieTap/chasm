package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ComponentFunction
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ScriptInstance
import io.github.charlietap.chasm.script.value.ComponentValueMapper
import io.github.charlietap.chasm.script.value.ScriptValue
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
    componentValueMapper = ::ComponentValueMapper,
)

private fun InvokeActionRunner(
    context: ScriptContext,
    command: Command,
    action: InvokeAction,
    valueMapper: ValueMapper,
    componentValueMapper: ComponentValueMapper,
): ActionResult {
    return when (val instance = context.instances[action.moduleName]!!) {
        is ScriptInstance.Core -> {
            val result = invoke(
                context.store,
                instance.instance,
                action.field,
                action.args.mapNotNull(valueMapper),
            )
            result.fold(
                onSuccess = { results -> ActionResult.Success(results.map(ScriptValue::Core)) },
                onError = { error -> ActionResult.Failure(command, "invoke action returned an error", error) },
            )
        }
        is ScriptInstance.ComponentModel -> {
            val function = instance.instance.exports
                .firstOrNull { export -> export.name == action.field }
                ?.value as? ComponentFunction
                ?: return ActionResult.Failure(command, "component function export not found")
            if (action.args.size != function.type.params.size) {
                return ActionResult.Failure(command, "component argument count does not match function type")
            }
            val arguments = action.args.mapIndexed { index, value ->
                componentValueMapper(value, function.type.params[index].type)
                    ?: return ActionResult.Failure(command, "component argument does not match function type")
            }
            val result = invoke(context.store, function, arguments)
            result.fold(
                onSuccess = { results ->
                    ActionResult.Success(
                        value = results.map(ScriptValue::ComponentModel),
                        componentResultType = function.type.result,
                    )
                },
                onError = { error -> ActionResult.Failure(command, "invoke action returned an error", error) },
            )
        }
    }
}
