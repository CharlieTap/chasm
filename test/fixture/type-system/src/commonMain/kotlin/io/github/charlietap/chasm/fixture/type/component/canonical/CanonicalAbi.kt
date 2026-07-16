package io.github.charlietap.chasm.fixture.type.component.canonical

import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiContext
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDeferredType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDescriptor
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiProperties
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiSignatureOptions
import io.github.charlietap.chasm.type.component.canonical.CanonicalFunctionTypeLowering

fun canonicalAbiProperties(
    containsString: Boolean = false,
    containsDynamicList: Boolean = false,
    containsResource: Boolean = false,
    containsBorrow: Boolean = false,
    deferredTypes: Set<CanonicalAbiDeferredType> = emptySet(),
) = CanonicalAbiProperties(
    containsString = containsString,
    containsDynamicList = containsDynamicList,
    containsResource = containsResource,
    containsBorrow = containsBorrow,
    deferredTypes = deferredTypes,
)

fun canonicalAbiShape(
    flatTypes: List<ValueType> = emptyList(),
    properties: CanonicalAbiProperties = canonicalAbiProperties(),
) = CanonicalAbiShape(
    flatTypes = flatTypes,
    properties = properties,
)

fun canonicalAbiDescriptor(
    type: DefinedType = definedType(),
    requiresMemory: Boolean = false,
    requiresRealloc: Boolean = false,
) = CanonicalAbiDescriptor(
    type = type,
    requiresMemory = requiresMemory,
    requiresRealloc = requiresRealloc,
)

fun canonicalAbiDescriptor(
    functionType: ComponentFunctionType,
    context: CanonicalAbiContext,
    options: CanonicalAbiSignatureOptions = CanonicalAbiSignatureOptions(),
): CanonicalAbiDescriptor {
    val lowering = requireNotNull(CanonicalFunctionTypeLowering(functionType, options, context))
    return canonicalAbiDescriptor(
        type = lowering.type,
        requiresMemory = lowering.requiresMemory,
        requiresRealloc = lowering.requiresRealloc,
    )
}

fun canonicalAbiSignatureOptions(
    addressType: AddressType = AddressType.I32,
    async: Boolean = false,
    hasCallback: Boolean = false,
) = CanonicalAbiSignatureOptions(
    addressType = addressType,
    async = async,
    hasCallback = hasCallback,
)
