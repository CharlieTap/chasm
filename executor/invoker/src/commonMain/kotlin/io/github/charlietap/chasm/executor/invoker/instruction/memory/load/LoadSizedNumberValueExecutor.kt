package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias SizedNumberValueReader<T> = LinearMemory.(Int, Int) -> Result<T, InvocationError>

internal typealias LoadSizedNumberValueExecutor<T> = (Store, Stack, MemArg, Int, SizedNumberValueReader<T>, Constructor<T>) -> Result<Unit, InvocationError>
