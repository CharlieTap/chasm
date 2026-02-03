package io.github.charlietap.chasm.gradle

enum class Mode {
    CONSUMER,

    @Deprecated(
        message = "Producer mode is deprecated and will be removed in a future release. Use https://github.com/CharlieTap/glueball instead.",
    )
    PRODUCER,
}
