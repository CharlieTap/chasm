package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.chasm.script.ext.mismatchTemplate
import io.github.charlietap.chasm.script.value.ValueMapper
import io.github.charlietap.chasm.script.value.ValueMatcher
import io.github.charlietap.sweet.lib.command.AssertReturnCommand

typealias AssertReturnCommandRunner = (ScriptContext, AssertReturnCommand) -> CommandResult

fun AssertReturnCommandRunner(
    context: ScriptContext,
    command: AssertReturnCommand,
): CommandResult = AssertReturnCommandRunner(
    context,
    command,
    ::ActionRunner,
    ::ValueMapper,
    ::ValueMatcher,
)

fun AssertReturnCommandRunner(
    context: ScriptContext,
    command: AssertReturnCommand,
    actionRunner: ActionRunner,
    valueMapper: ValueMapper,
    valueMatcher: ValueMatcher,
): CommandResult {
    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            val expected = command.expected.mapNotNull(valueMapper)

            val resultsMatch = compareResults(expected, result.value, valueMatcher)

            if (resultsMatch) {
                CommandResult.Success
            } else {
                val mismatch = mismatchTemplate(expected, result.value)
                CommandResult.Failure(command, mismatch)
            }
        }
        is ActionResult.Failure -> {
            println(result)
            CommandResult.Failure(command, result.reason)
        }
    }
}

private fun compareResults(
    expected: List<ExecutionValue>,
    actual: List<ExecutionValue>,
    valueMatcher: ValueMatcher,
): Boolean = if (expected.size == actual.size) {
    expected.zip(actual).all { (first, second) ->
        valueMatcher(first, second)
    }
} else {
    false
}
