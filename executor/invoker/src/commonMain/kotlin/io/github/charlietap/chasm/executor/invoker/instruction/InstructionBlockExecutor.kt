package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias InstructionBlockExecutor = (Stack, Stack.Entry.Label, List<ExecutionInstruction>, List<ExecutionValue>) -> Result<Unit, InvocationError>
