@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.jit.internal._PreparedObjectCall
import io.github.charlietap.chasm.jit.internal._prepareObjectCall
import io.github.charlietap.chasm.type.FunctionType

@ExperimentalChasmApi
class PreparedCall internal constructor(
    private val lane: _PreparedObjectCall,
) {

    val type: FunctionType
        get() = lane.type

    val slots: Int
        get() = lane.slots

    fun call(
        stack: HostStack,
        references: ReferenceSlots,
    ): ChasmResult<Unit, ChasmError.ExecutionError> {
        return lane.call(stack, references.objects)
    }
}

@ExperimentalChasmApi
fun prepareCall(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<PreparedCall, ChasmError.ExecutionError> {
    return when (val prepared = _prepareObjectCall(store, instance, name)) {
        is ChasmResult.Success -> ChasmResult.Success(PreparedCall(prepared.result))
        is ChasmResult.Error -> prepared
    }
}
