package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias I32StoreExecutor = (Store, Stack, MemoryInstruction.I32Store) -> Result<Unit, InvocationError>
