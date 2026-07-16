package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.runtime.value.component.ComponentValue

fun ComponentValueMatcher(
    expected: ComponentValue,
    actual: ComponentValue,
): Boolean = when {
    expected is ComponentValue.F32 && actual is ComponentValue.F32 ->
        expected.value.isNaN() && actual.value.isNaN() || expected.value == actual.value
    expected is ComponentValue.F64 && actual is ComponentValue.F64 ->
        expected.value.isNaN() && actual.value.isNaN() || expected.value == actual.value
    expected is ComponentValue.Record && actual is ComponentValue.Record ->
        componentListsMatch(expected.fields, actual.fields)
    expected is ComponentValue.Tuple && actual is ComponentValue.Tuple ->
        componentListsMatch(expected.elements, actual.elements)
    expected is ComponentValue.ListValue && actual is ComponentValue.ListValue ->
        componentListsMatch(expected.elements, actual.elements)
    expected is ComponentValue.Variant && actual is ComponentValue.Variant ->
        expected.caseIndex == actual.caseIndex && componentPayloadMatches(expected.value, actual.value)
    expected is ComponentValue.Option.Some && actual is ComponentValue.Option.Some ->
        ComponentValueMatcher(expected.value, actual.value)
    expected is ComponentValue.Result.Ok && actual is ComponentValue.Result.Ok ->
        componentPayloadMatches(expected.value, actual.value)
    expected is ComponentValue.Result.Error && actual is ComponentValue.Result.Error ->
        componentPayloadMatches(expected.value, actual.value)
    else -> expected == actual
}

private fun componentListsMatch(
    expected: List<ComponentValue>,
    actual: List<ComponentValue>,
): Boolean = expected.size == actual.size && expected.indices.all { index ->
    ComponentValueMatcher(expected[index], actual[index])
}

private fun componentPayloadMatches(
    expected: ComponentValue?,
    actual: ComponentValue?,
): Boolean = when {
    expected == null -> actual == null
    actual == null -> false
    else -> ComponentValueMatcher(expected, actual)
}
