package io.github.charlietap.chasm.decoder.parallel

/** Whether function bodies can be shown to the module's observer, which is not known while [PENDING]. */
internal enum class ObserverState {
    PENDING,
    FOLLOWING,
    NONE,
}
