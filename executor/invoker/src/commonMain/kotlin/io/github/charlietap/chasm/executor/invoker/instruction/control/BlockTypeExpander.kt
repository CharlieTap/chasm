package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance

internal typealias BlockTypeExpander = (ModuleInstance, ControlInstruction.BlockType) -> Result<FunctionType?, InvocationError>
