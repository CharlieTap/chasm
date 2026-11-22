package io.github.charlietap.chasm.host

@MustBeDocumented
@RequiresOptIn(
    message = "This API exposes unsafe access to runtime state. " +
        "The caller is responsible for its mutation and lifetime.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS,
)
annotation class UnsafeHostApi
