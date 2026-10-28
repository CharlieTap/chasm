package io.github.charlietap.chasm.executor.invoker.dispatch.table

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ElementInstance

fun ElemDropDispatcher(
    element: ElementInstance,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    run {
        element.elements = longArrayOf()
    }
    nextIp
}
