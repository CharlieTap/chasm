package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.DataInstance

@InternalChasmApi
fun DataDropDispatcher(
    data: DataInstance,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    run {
        data.bytes = DataInstance.EMPTY
    }
    nextIp
}
