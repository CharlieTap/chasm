package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal typealias NumberValueReader<T> = (MemoryInstance, Int, Int) -> Result<T, InvocationError.MemoryOperationOutOfBounds>

internal typealias Constructor<T> = (T) -> NumberValue<T>

internal typealias LoadNumberValueExecutor<T> = (Store, Stack, MemArg, Int, NumberValueReader<T>, Constructor<T>) -> Result<Unit, InvocationError>
