package io.github.charlietap.chasm.embedding.fixture

internal actual fun collectPlatformGarbage() {
    System.gc()
}
