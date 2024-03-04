package io.github.charlietap.chasm.executor.invoker.instruction.numeric

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias NumericInstructionExecutor = (NumericInstruction, Stack) -> Result<Unit, InvocationError>
