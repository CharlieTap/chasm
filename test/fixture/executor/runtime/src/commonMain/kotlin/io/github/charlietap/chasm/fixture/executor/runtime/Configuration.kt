package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.store.Store

fun configuration(
    store: Store = store(),
    thread: Thread = thread(),
): Configuration = Configuration(
    store = store,
    thread = thread,
)
