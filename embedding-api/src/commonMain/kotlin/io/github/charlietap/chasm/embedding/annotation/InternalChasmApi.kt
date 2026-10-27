package io.github.charlietap.chasm

@MustBeDocumented
@RequiresOptIn(
    message = "This is an internal integration API for official Chasm artifacts. " +
        "It is not intended to be called directly. No source, binary, or behavioral compatibility is guaranteed.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalChasmApi
