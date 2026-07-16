package io.github.charlietap.chasm.runtime.component.canonical

import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex

data class LinearMemoryCanonicalOptions(
    val optionOwner: RuntimeComponentInstanceIndex,
    val encoding: CanonicalStringEncoding = CanonicalStringEncoding.Utf8,
    val memorySlot: Int = ABSENT_CANONICAL_OPTION_SLOT,
    val reallocSlot: Int = ABSENT_CANONICAL_OPTION_SLOT,
    val postReturnSlot: Int = ABSENT_CANONICAL_OPTION_SLOT,
) {
    val hasMemory: Boolean
        get() = memorySlot != ABSENT_CANONICAL_OPTION_SLOT

    val hasRealloc: Boolean
        get() = reallocSlot != ABSENT_CANONICAL_OPTION_SLOT

    val hasPostReturn: Boolean
        get() = postReturnSlot != ABSENT_CANONICAL_OPTION_SLOT
}

enum class CanonicalStringEncoding {
    Utf8,
    Utf16,
    Latin1Utf16,
}

private const val ABSENT_CANONICAL_OPTION_SLOT = -1
