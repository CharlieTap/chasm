package io.github.charlietap.chasm.executor.invoker.dispatch

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

typealias Dispatcher<T> = (T) -> DispatchableInstruction
