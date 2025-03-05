package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

typealias TypeOf<S, T> = (S, Store, ModuleInstance) -> T
