package io.github.charlietap.chasm.runtime.dispatch

import io.github.charlietap.chasm.runtime.execution.ExecutionContext

typealias DispatchableInstruction = (ExecutionContext) -> Unit
