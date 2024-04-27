package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.expect
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult.Success
import io.github.charlietap.chasm.script.value.ValueMapper
import io.github.charlietap.sweet.lib.action.InvokeAction

typealias InvokeActionRunner = (ScriptContext, InvokeAction) -> ActionResult

fun InvokeActionRunner(
    context: ScriptContext,
    action: InvokeAction,
) = InvokeActionRunner(
    context = context,
    action = action,
    valueMapper = ::ValueMapper,
)

private fun InvokeActionRunner(
    context: ScriptContext,
    action: InvokeAction,
    valueMapper: ValueMapper,
): ActionResult {
    return invoke(
        context.store,
        context.instances[action.moduleName]!!,
        action.field,
        action.args.mapNotNull(valueMapper),
    ).expect("Invoke failed").let(::Success)
}
