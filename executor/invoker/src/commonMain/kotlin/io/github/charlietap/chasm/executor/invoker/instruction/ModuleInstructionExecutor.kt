package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ModuleInstructionExecutor = (ModuleInstruction, Store, Stack) -> Result<Unit, InvocationError>
