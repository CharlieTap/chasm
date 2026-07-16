package io.github.charlietap.chasm.script.command

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import io.github.charlietap.chasm.script.ScriptContext
import io.github.charlietap.chasm.script.action.ActionResult
import io.github.charlietap.chasm.script.action.ActionRunner
import io.github.charlietap.chasm.script.ext.mismatchTemplate
import io.github.charlietap.chasm.script.value.ComponentValueMapper
import io.github.charlietap.chasm.script.value.ComponentValueMatcher
import io.github.charlietap.chasm.script.value.ScriptValue
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
    ::ComponentValueMapper,
    ::ComponentValueMatcher,
)

fun AssertReturnCommandRunner(
    context: ScriptContext,
    command: AssertReturnCommand,
    actionRunner: ActionRunner,
    valueMapper: ValueMapper,
    valueMatcher: ValueMatcher,
    componentValueMapper: ComponentValueMapper,
    componentValueMatcher: (ComponentValue, ComponentValue) -> Boolean,
): CommandResult {
    return when (val result = actionRunner(context, command, command.action)) {
        is ActionResult.Success -> {
            val resultsMatch = compareResults(
                expected = command.expected,
                actual = result.value,
                componentResultType = result.componentResultType,
                valueMapper = valueMapper,
                valueMatcher = valueMatcher,
                componentValueMapper = componentValueMapper,
                componentValueMatcher = componentValueMatcher,
            )

            if (resultsMatch) {
                CommandResult.Success
            } else {
                val mismatch = mismatchTemplate(command.expected, result.value)
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
    actual: List<ScriptValue>,
    componentResultType: io.github.charlietap.chasm.type.component.ComponentValueType?,
    valueMapper: ValueMapper,
    valueMatcher: ValueMatcher,
    componentValueMapper: ComponentValueMapper,
    componentValueMatcher: (ComponentValue, ComponentValue) -> Boolean,
): Boolean = if (expected.size == actual.size) {
    expected.zip(actual).all { (expectedValue, actualValue) ->
        when (actualValue) {
            is ScriptValue.Core -> matchValue(expectedValue, actualValue.value, valueMapper, valueMatcher)
            is ScriptValue.ComponentModel -> {
                val resultType = componentResultType ?: return@all false
                val mapped = componentValueMapper(expectedValue, resultType) ?: return@all false
                componentValueMatcher(mapped, actualValue.value)
            }
        }
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
