package io.github.charlietap.chasm.runtime.component.canonical

import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape

class LinearMemoryLayout(
    val kind: CanonicalLayoutKind,
    val shape: CanonicalAbiShape,
    val size32: UInt,
    val alignment32: UInt,
    val discriminantSize32: UInt = 0u,
    val payloadOffset32: UInt = 0u,
    val flagsWords: UInt = 0u,
    val elementCount: Int = 0,
    val children: IntArray = intArrayOf(),
    val offsets32: UIntArray = uintArrayOf(),
    val resourceType: RuntimeResourceTypeIndex? = null,
    val properties: CanonicalLayoutProperties = CanonicalLayoutProperties(),
)

enum class CanonicalLayoutKind {
    Bool,
    S8,
    U8,
    S16,
    U16,
    S32,
    U32,
    S64,
    U64,
    F32,
    F64,
    Char,
    String,
    Record,
    Variant,
    List,
    Tuple,
    Flags,
    Enum,
    Option,
    Result,
    Own,
    Borrow,
}

data class CanonicalLayoutProperties(
    val containsString: Boolean = false,
    val containsList: Boolean = false,
    val containsResource: Boolean = false,
    val containsBorrow: Boolean = false,
    val liftMayAllocate: Boolean = false,
    val lowerMayAllocate: Boolean = false,
    val canUseBulkMemory: Boolean = false,
)
