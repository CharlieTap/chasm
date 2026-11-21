package io.github.charlietap.chasm.bindgen

data class ExportedAllocator(
    val allocationFunction: String,
    val deallocationFunction: String,
)

data class StringAllocationStrategy(
    val freeAfterCall: Boolean,
)

/**
 * Defines how strings are encoded when passed to or returned from WASM functions.
 */
enum class StringEncodingStrategy {
    /**
     * The pointer and length of the string are encoded in two I32s
     */
    POINTER_AND_LENGTH,

    /**
     * The pointer of the string is encoded in an I32 and end of the String is the first null byte
     * encountered after that pointer
     */
    NULL_TERMINATED,

    /**
     * The pointer of the string is encoded in an I32 and length of the string is encoded in an integer (4 bytes)
     * found at that pointer with the bytes of the string following
     */
    LENGTH_PREFIXED,

    /**
     * The pointer and length of the string are encoded in a single I64
     */
    PACKED_POINTER_AND_LENGTH,

    GC_UTF16_ARRAY,

    GC_UTF8_ARRAY,
}

internal val StringEncodingStrategy?.storedInGcArray: Boolean
    get() {
        return this == StringEncodingStrategy.GC_UTF16_ARRAY || this == StringEncodingStrategy.GC_UTF8_ARRAY
    }
