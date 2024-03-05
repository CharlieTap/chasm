package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias LocalTeeExecutor = (Stack, VariableInstruction.LocalTee) -> Result<Unit, InvocationError>
