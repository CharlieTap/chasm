package io.github.charlietap.chasm.fixture.runtime

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.store.Store

fun configuration(
    store: Store = store(),
    thread: Thread = thread(),
    config: RuntimeConfig = runtimeConfig(),
): Configuration = Configuration(
    store = store,
    thread = thread,
    config = config,
)
