package io.github.charlietap.chasm.fixture.runtime.component.canonical

import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiContext
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDescriptor
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiSignatureOptions
import io.github.charlietap.chasm.type.component.canonical.CanonicalFunctionTypeLowering
import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiDescriptor as canonicalAbiDescriptorFixture

fun canonicalAbiDescriptorFor(
    functionType: ComponentFunctionType,
    context: CanonicalAbiContext,
): CanonicalAbiDescriptor {
    val lowering = requireNotNull(
        CanonicalFunctionTypeLowering(functionType, CanonicalAbiSignatureOptions(), context),
    )
    return canonicalAbiDescriptorFixture(
        type = lowering.type,
        requiresMemory = lowering.requiresMemory,
        requiresRealloc = lowering.requiresRealloc,
    )
}
