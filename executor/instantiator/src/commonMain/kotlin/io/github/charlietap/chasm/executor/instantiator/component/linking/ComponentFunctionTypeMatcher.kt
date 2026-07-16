package io.github.charlietap.chasm.executor.instantiator.component.linking

import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentDefinedValueType
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentValueType

internal fun ComponentFunctionTypeMatcher(
    actual: ComponentFunctionType,
    expected: ComponentFunctionType,
    resourceTypeMatcher: (ComponentResourceTypeId, ComponentResourceTypeId) -> Boolean,
): Boolean {
    if (actual.async != expected.async || actual.params.size != expected.params.size) return false
    actual.params.indices.forEach { index ->
        val actualParameter = actual.params[index]
        val expectedParameter = expected.params[index]
        if (actualParameter.label != expectedParameter.label) return false
        if (!componentValueTypeMatches(actualParameter.type, expectedParameter.type, resourceTypeMatcher)) return false
    }
    return optionalComponentValueTypeMatches(actual.result, expected.result, resourceTypeMatcher)
}

private fun componentValueTypeMatches(
    actual: ComponentValueType,
    expected: ComponentValueType,
    resourceTypeMatcher: (ComponentResourceTypeId, ComponentResourceTypeId) -> Boolean,
): Boolean {
    return when {
        actual is ComponentValueType.Primitive && expected is ComponentValueType.Primitive ->
            actual.type == expected.type
        actual is ComponentValueType.Defined && expected is ComponentValueType.Defined ->
            componentDefinedValueTypeMatches(
                actual.definition.type.valueType() ?: return false,
                expected.definition.type.valueType() ?: return false,
                resourceTypeMatcher,
            )
        actual is ComponentValueType.Primitive && expected is ComponentValueType.Defined ->
            actual.type == expected.definition.type.primitiveType()
        actual is ComponentValueType.Defined && expected is ComponentValueType.Primitive ->
            actual.definition.type.primitiveType() == expected.type
        else -> false
    }
}

private fun componentDefinedValueTypeMatches(
    actual: ComponentDefinedValueType,
    expected: ComponentDefinedValueType,
    resourceTypeMatcher: (ComponentResourceTypeId, ComponentResourceTypeId) -> Boolean,
): Boolean = when {
    actual is ComponentDefinedValueType.Primitive && expected is ComponentDefinedValueType.Primitive ->
        actual.type == expected.type
    actual is ComponentDefinedValueType.Record && expected is ComponentDefinedValueType.Record ->
        actual.fields.size == expected.fields.size && actual.fields.indices.all { index ->
            val actualField = actual.fields[index]
            val expectedField = expected.fields[index]
            actualField.label == expectedField.label &&
                componentValueTypeMatches(actualField.type, expectedField.type, resourceTypeMatcher)
        }
    actual is ComponentDefinedValueType.Variant && expected is ComponentDefinedValueType.Variant ->
        actual.cases.size == expected.cases.size && actual.cases.indices.all { index ->
            val actualCase = actual.cases[index]
            val expectedCase = expected.cases[index]
            actualCase.label == expectedCase.label &&
                optionalComponentValueTypeMatches(actualCase.type, expectedCase.type, resourceTypeMatcher)
        }
    actual is ComponentDefinedValueType.ListValue && expected is ComponentDefinedValueType.ListValue ->
        componentValueTypeMatches(actual.element, expected.element, resourceTypeMatcher)
    actual is ComponentDefinedValueType.FixedLengthList && expected is ComponentDefinedValueType.FixedLengthList ->
        actual.length == expected.length &&
            componentValueTypeMatches(actual.element, expected.element, resourceTypeMatcher)
    actual is ComponentDefinedValueType.Map && expected is ComponentDefinedValueType.Map ->
        actual.key == expected.key && componentValueTypeMatches(actual.value, expected.value, resourceTypeMatcher)
    actual is ComponentDefinedValueType.Tuple && expected is ComponentDefinedValueType.Tuple ->
        actual.elements.size == expected.elements.size && actual.elements.indices.all { index ->
            componentValueTypeMatches(actual.elements[index], expected.elements[index], resourceTypeMatcher)
        }
    actual is ComponentDefinedValueType.Flags && expected is ComponentDefinedValueType.Flags ->
        actual.labels == expected.labels
    actual is ComponentDefinedValueType.Enum && expected is ComponentDefinedValueType.Enum ->
        actual.labels == expected.labels
    actual is ComponentDefinedValueType.Option && expected is ComponentDefinedValueType.Option ->
        componentValueTypeMatches(actual.value, expected.value, resourceTypeMatcher)
    actual is ComponentDefinedValueType.Result && expected is ComponentDefinedValueType.Result ->
        optionalComponentValueTypeMatches(actual.ok, expected.ok, resourceTypeMatcher) &&
            optionalComponentValueTypeMatches(actual.error, expected.error, resourceTypeMatcher)
    actual is ComponentDefinedValueType.Own && expected is ComponentDefinedValueType.Own ->
        resourceTypeMatcher(actual.resource, expected.resource)
    actual is ComponentDefinedValueType.Borrow && expected is ComponentDefinedValueType.Borrow ->
        resourceTypeMatcher(actual.resource, expected.resource)
    actual is ComponentDefinedValueType.Stream && expected is ComponentDefinedValueType.Stream ->
        optionalComponentValueTypeMatches(actual.element, expected.element, resourceTypeMatcher)
    actual is ComponentDefinedValueType.Future && expected is ComponentDefinedValueType.Future ->
        optionalComponentValueTypeMatches(actual.value, expected.value, resourceTypeMatcher)
    else -> false
}

private fun optionalComponentValueTypeMatches(
    actual: ComponentValueType?,
    expected: ComponentValueType?,
    resourceTypeMatcher: (ComponentResourceTypeId, ComponentResourceTypeId) -> Boolean,
): Boolean = when {
    actual == null && expected == null -> true
    actual != null && expected != null -> componentValueTypeMatches(actual, expected, resourceTypeMatcher)
    else -> false
}

private fun ComponentDefinedType.valueType(): ComponentDefinedValueType? =
    (this as? ComponentDefinedType.Value)?.type

private fun ComponentDefinedType.primitiveType() =
    (valueType() as? ComponentDefinedValueType.Primitive)?.type
