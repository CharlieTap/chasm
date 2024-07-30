package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias StackValuePopper<T> = Stack.() -> Result<T, InvocationError>
internal typealias NumberValueWriter<T> = (MemoryInstance, T, Int, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

internal typealias StoreNumberValueExecutor<T> = (Store, Stack, Index.MemoryIndex, MemArg, Int, StackValuePopper<T>, NumberValueWriter<T>) -> Result<Unit, InvocationError>
