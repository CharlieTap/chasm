package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.stack.ValueStack

typealias StoreOp = (Long, ValueStack) -> Unit
