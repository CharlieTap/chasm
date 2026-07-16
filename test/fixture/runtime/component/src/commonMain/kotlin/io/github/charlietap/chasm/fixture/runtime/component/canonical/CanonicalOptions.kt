package io.github.charlietap.chasm.fixture.runtime.component.canonical

import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryCanonicalOptions
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex

fun linearMemoryCanonicalOptions(
    optionOwner: RuntimeComponentInstanceIndex = runtimeComponentInstanceIndex(),
    encoding: CanonicalStringEncoding = CanonicalStringEncoding.Utf8,
    memorySlot: Int = -1,
    reallocSlot: Int = -1,
    postReturnSlot: Int = -1,
) = LinearMemoryCanonicalOptions(
    optionOwner = optionOwner,
    encoding = encoding,
    memorySlot = memorySlot,
    reallocSlot = reallocSlot,
    postReturnSlot = postReturnSlot,
)
