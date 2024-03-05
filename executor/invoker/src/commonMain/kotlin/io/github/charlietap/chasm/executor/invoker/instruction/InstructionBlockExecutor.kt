package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias InstructionBlockExecutor = (Store, Stack, Stack.Entry.Label, List<Instruction>, List<ExecutionValue>) -> Result<Unit, InvocationError>
