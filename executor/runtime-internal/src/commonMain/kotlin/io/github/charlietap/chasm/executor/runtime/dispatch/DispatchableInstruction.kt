package io.github.charlietap.chasm.executor.runtime.dispatch

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext

typealias DispatchableInstruction = (ExecutionContext) -> Unit
