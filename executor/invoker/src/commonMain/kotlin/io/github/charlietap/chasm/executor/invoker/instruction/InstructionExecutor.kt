package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias InstructionExecutor = (Instruction, Store, Stack) -> Result<Unit, InvocationError>
