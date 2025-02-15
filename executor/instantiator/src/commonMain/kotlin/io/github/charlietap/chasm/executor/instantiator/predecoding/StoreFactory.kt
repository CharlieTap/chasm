package io.github.charlietap.chasm.executor.instantiator.predecoding

import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.globalAddress
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instruction.StoreOp
import io.github.charlietap.chasm.ir.instruction.FusedDestination

internal typealias StoreFactory = (InstantiationContext, FusedDestination) -> StoreOp

internal inline fun StoreFactory(
    context: InstantiationContext,
    destination: FusedDestination,
): StoreOp {
    return context.storeCache.getOrPut(destination) {
        when (destination) {
            is FusedDestination.GlobalSet -> {
                val address = context.instance!!.globalAddress(destination.index).value
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
