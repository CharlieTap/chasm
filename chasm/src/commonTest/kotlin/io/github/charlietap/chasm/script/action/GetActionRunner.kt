package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.ScriptInstance
import io.github.charlietap.chasm.script.value.ScriptValue
import io.github.charlietap.sweet.lib.action.GetAction
import io.github.charlietap.sweet.lib.command.Command

typealias GetActionRunner = (ScriptContext, Command, GetAction) -> ActionResult

fun GetActionRunner(
    context: ScriptContext,
    command: Command,
    action: GetAction,
): ActionResult {

    val instance = context.instances[action.moduleName] as? ScriptInstance.Core
        ?: return ActionResult.Failure(command, "component instances do not expose core globals")
    val exports = exports(instance.instance)
    val global = (
        exports
            .firstOrNull {
                it.value is Global && it.name == action.field
            }?.value as? Global
    ) ?: return ActionResult.Failure(command, "exported global not found")

    val result = readGlobal(context.store, global)

    return result.fold(
        { globalValue ->
            ActionResult.Success(listOf(ScriptValue.Core(globalValue)))
        },
    ) { error ->
        ActionResult.Failure(command, "get action returned an error", error)
    }
}
