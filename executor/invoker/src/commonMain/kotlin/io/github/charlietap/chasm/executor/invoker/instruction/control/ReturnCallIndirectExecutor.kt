package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.HostFunctionCall
import io.github.charlietap.chasm.executor.invoker.function.ReturnWasmFunctionCall
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ReturnCallIndirectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.ReturnCallIndirect,
) = CallIndirectExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    table = instruction.table,
    type = instruction.type,
    hostFunctionCall = ::HostFunctionCall,
    wasmFunctionCall = ::ReturnWasmFunctionCall,
)
