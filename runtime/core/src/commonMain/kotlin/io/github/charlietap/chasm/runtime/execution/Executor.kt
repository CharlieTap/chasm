package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

typealias Executor<T> = (ValueStack, ControlStack, Store, ExecutionContext, T) -> Unit
