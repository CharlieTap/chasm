package io.github.charlietap.chasm.fixture.runtime.component.canonical

import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiShape
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutProperties
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape

fun canonicalLayoutProperties(
    containsString: Boolean = false,
    containsList: Boolean = false,
    containsResource: Boolean = false,
    containsBorrow: Boolean = false,
    liftMayAllocate: Boolean = false,
    lowerMayAllocate: Boolean = false,
    canUseBulkMemory: Boolean = false,
) = CanonicalLayoutProperties(
    containsString = containsString,
    containsList = containsList,
    containsResource = containsResource,
    containsBorrow = containsBorrow,
    liftMayAllocate = liftMayAllocate,
    lowerMayAllocate = lowerMayAllocate,
    canUseBulkMemory = canUseBulkMemory,
)

fun linearMemoryLayout(
    kind: CanonicalLayoutKind = CanonicalLayoutKind.Bool,
    shape: CanonicalAbiShape = canonicalAbiShape(),
    size32: UInt = 1u,
    alignment32: UInt = 1u,
    discriminantSize32: UInt = 0u,
    payloadOffset32: UInt = 0u,
    flagsWords: UInt = 0u,
    elementCount: Int = 0,
    children: IntArray = intArrayOf(),
    offsets32: UIntArray = uintArrayOf(),
    properties: CanonicalLayoutProperties = canonicalLayoutProperties(),
    resourceType: RuntimeResourceTypeIndex? = null,
) = LinearMemoryLayout(
    kind = kind,
    shape = shape,
    size32 = size32,
    alignment32 = alignment32,
    discriminantSize32 = discriminantSize32,
    payloadOffset32 = payloadOffset32,
    flagsWords = flagsWords,
    elementCount = elementCount,
    children = children,
    offsets32 = offsets32,
    properties = properties,
    resourceType = resourceType,
)
