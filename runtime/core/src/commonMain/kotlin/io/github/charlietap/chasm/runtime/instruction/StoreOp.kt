package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.stack.ValueStack

typealias StoreOp = (Long, ValueStack) -> Unit
