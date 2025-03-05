package io.github.charlietap.chasm.predecoder

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.predecoder.ext.globalAddress
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.instruction.StoreOp

internal typealias StoreFactory = (PredecodingContext, FusedDestination) -> StoreOp

internal inline fun StoreFactory(
    context: PredecodingContext,
    destination: FusedDestination,
): StoreOp {
    return context.storeCache.getOrPut(destination) {
        when (destination) {
            is FusedDestination.GlobalSet -> {
                val address = context.instance.globalAddress(destination.index).value
                val global = context.store.global(address);
                { value, _ -> global.value = value }
            }
            is FusedDestination.LocalSet -> {
                { value, stack -> stack.setLocal(destination.index.idx, value) }
            }
            FusedDestination.ValueStack -> {
                { value, stack -> stack.push(value) }
            }
        }
    }
}
