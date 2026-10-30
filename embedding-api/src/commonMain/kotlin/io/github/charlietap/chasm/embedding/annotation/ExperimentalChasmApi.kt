package io.github.charlietap.chasm

@MustBeDocumented
@RequiresOptIn(
    message = "This API is experimental. It may change or be removed in a future release, " +
        "with no source, binary, or behavioral compatibility guarantees.",
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
)
annotation class ExperimentalChasmApi
