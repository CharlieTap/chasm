package io.github.charlietap.chasm.script.action

import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.sweet.lib.action.Action
import io.github.charlietap.sweet.lib.action.GetAction
import io.github.charlietap.sweet.lib.action.InvokeAction
import io.github.charlietap.sweet.lib.command.Command

typealias ActionRunner = (ScriptContext, Command, Action) -> ActionResult

fun ActionRunner(
    context: ScriptContext,
    command: Command,
    action: Action,
): ActionResult = ActionRunner(
    context = context,
    command = command,
    action = action,
    invokeActionRunner = ::InvokeActionRunner,
    getActionRunner = ::GetActionRunner,
)

private fun ActionRunner(
    context: ScriptContext,
    command: Command,
    action: Action,
    invokeActionRunner: InvokeActionRunner,
    getActionRunner: GetActionRunner,
): ActionResult = when (action) {
    is InvokeAction -> invokeActionRunner(context, action)
    is GetAction -> getActionRunner(context, command, action)
}
