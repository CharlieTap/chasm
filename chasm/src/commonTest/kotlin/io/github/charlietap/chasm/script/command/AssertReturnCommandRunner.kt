package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.chasm.script.ext.mismatchTemplate
import io.github.charlietap.chasm.script.value.ValueMapper
import io.github.charlietap.chasm.script.value.ValueMatcher
import io.github.charlietap.sweet.lib.command.AssertReturnCommand
import io.github.charlietap.sweet.lib.value.Value

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
            val resultsMatch = compareResults(command.expected, result.value, valueMapper, valueMatcher)

            if (resultsMatch) {
                CommandResult.Success
            } else {
                val expected = command.expected.mapNotNull(valueMapper)
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
    expected: List<Value>,
    actual: List<ExecutionValue>,
    valueMapper: ValueMapper,
    valueMatcher: ValueMatcher,
): Boolean = if (expected.size == actual.size) {
    expected.zip(actual).all { (expectedValue, actualValue) ->
        matchValue(expectedValue, actualValue, valueMapper, valueMatcher)
    }
} else {
    false
}

private fun matchValue(
    expected: Value,
    actual: ExecutionValue,
    valueMapper: ValueMapper,
    valueMatcher: ValueMatcher,
): Boolean = when (expected) {
    is Value.Either -> expected.values.any { alternative ->
        matchValue(alternative, actual, valueMapper, valueMatcher)
    }
    else -> {
        val mappedExpected = valueMapper(expected)
        mappedExpected != null && valueMatcher(mappedExpected, actual)
    }
}
