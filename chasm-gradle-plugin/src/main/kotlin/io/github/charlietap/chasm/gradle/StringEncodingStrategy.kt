package io.github.charlietap.chasm.gradle

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
     * The pointer of the string is encoded in an I32 and length of the string is encoded in the byte
     * found at that pointer
     */
    LENGTH_PREFIXED,

    /**
     * The pointer and length of the string are encoded in a single I64
     */
    PACKED_POINTER_AND_LENGTH,
}
