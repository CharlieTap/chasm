package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.runtime.instance.GlobalInstance

@InternalChasmApi
fun GlobalGetSDispatcher(
    global: GlobalInstance,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, global.value)
    }
}

@InternalChasmApi
fun GlobalSetIDispatcher(
    value: Long,
    global: GlobalInstance,
) = dispatchInstruction { vstack, context ->
    run {
        global.value = value
    }
}

@InternalChasmApi
fun GlobalSetSDispatcher(
    sourceSlot: Int,
    global: GlobalInstance,
) = dispatchInstruction { vstack, context ->
    run {
        global.value = vstack.getFrameSlot(sourceSlot)
    }
}
