package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.function.LocalInitialization
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun initializeLocals(
    vstack: ValueStack,
    strategy: WasmFunctionCallStrategy,
    fp: Int,
) {
    val firstSlot = strategy.interfaceSlotCount + 1
    when (val init = strategy.localInitialization) {
        LocalInitialization.None -> Unit
        LocalInitialization.Zero1 -> vstack.setFrameSlot(fp, firstSlot, 0L)
        LocalInitialization.Zero2 -> {
            vstack.setFrameSlot(fp, firstSlot, 0L)
            vstack.setFrameSlot(fp, firstSlot + 1, 0L)
        }
        LocalInitialization.Zero3 -> {
            vstack.setFrameSlot(fp, firstSlot, 0L)
            vstack.setFrameSlot(fp, firstSlot + 1, 0L)
            vstack.setFrameSlot(fp, firstSlot + 2, 0L)
        }
        LocalInitialization.Zero4 -> {
            vstack.setFrameSlot(fp, firstSlot, 0L)
            vstack.setFrameSlot(fp, firstSlot + 1, 0L)
            vstack.setFrameSlot(fp, firstSlot + 2, 0L)
            vstack.setFrameSlot(fp, firstSlot + 3, 0L)
        }
        is LocalInitialization.ZeroRange -> vstack.fillFrameSlots(fp, firstSlot, init.count, 0L)
        is LocalInitialization.ConstantStores -> vstack.copyValuesToFrame(init.values, fp, firstSlot)
    }
}
