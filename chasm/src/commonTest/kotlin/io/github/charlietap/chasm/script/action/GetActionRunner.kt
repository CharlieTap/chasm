package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.sweet.lib.action.GetAction
import io.github.charlietap.sweet.lib.command.Command

typealias GetActionRunner = (ScriptContext, Command, GetAction) -> ActionResult

fun GetActionRunner(
    context: ScriptContext,
    command: Command,
    action: GetAction,
): ActionResult {
    val global = (
        context.instances[action.moduleName]!!.exports.firstOrNull {
            it.value is ExternalValue.Global && it.name.name == action.field
        }?.value as? ExternalValue.Global
    ) ?: return ActionResult.Failure(command, "exported global not found")

    val result = readGlobal(context.store, global.address)

    return result.fold(
        { globalValue ->
            ActionResult.Success(listOf(globalValue))
        },
    ) { error ->
        ActionResult.Failure(command, "get action returned an error", error)
    }
}
