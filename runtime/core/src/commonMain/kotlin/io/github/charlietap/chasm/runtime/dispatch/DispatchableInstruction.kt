package io.github.charlietap.chasm.runtime.dispatch

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

typealias DispatchableInstruction = (ValueStack, ControlStack, Store, ExecutionContext) -> Unit
