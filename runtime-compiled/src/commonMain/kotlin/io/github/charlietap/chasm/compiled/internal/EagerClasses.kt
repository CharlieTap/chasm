package io.github.charlietap.chasm.compiled.internal

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class EagerClasses(
    private val loader: ClassLoader?,
    private val classes: List<String>,
    private val initializer: CodeClassInitializer,
) {

    private val mutex = Mutex()

    @Volatile
    private var initialized = classes.isEmpty() || loader == null

    suspend fun initialize() {

        if (initialized) {
            return
        }

        mutex.withLock {
            if (!initialized) {
                initializer.initialize(checkNotNull(loader), classes)
                initialized = true
            }
        }
    }
}
