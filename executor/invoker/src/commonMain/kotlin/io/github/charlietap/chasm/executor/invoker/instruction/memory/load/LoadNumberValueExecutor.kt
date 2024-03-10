package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal typealias NumberValueReader<T> = LinearMemory.(Int) -> Result<T, InvocationError>

internal typealias Constructor<T> = (T) -> NumberValue<T>

internal typealias LoadNumberValueExecutor<T> = (Store, Stack, MemArg, NumberValueReader<T>, Constructor<T>) -> Result<Unit, InvocationError>
