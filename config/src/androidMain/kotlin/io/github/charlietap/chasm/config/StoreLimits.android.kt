package io.github.charlietap.chasm.config

// Growing to the cap keeps the old and new arrays live at once, 24 MiB at
// most here, which fits a default app heap. 2M slots is far deeper than
// legitimate recursion needs.
internal actual val DEFAULT_STACK_BYTES: Long? = 16L * 1024 * 1024
