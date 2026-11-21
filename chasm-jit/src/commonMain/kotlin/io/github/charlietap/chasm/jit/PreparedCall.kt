@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostStack
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.jit.internal._PreparedObjectCall
import io.github.charlietap.chasm.jit.internal._prepareObjectCall
import io.github.charlietap.chasm.reference.ExceptionReference
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
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return lane.call(stack, references.objects)
    }

    @UnsafeHostApi
    fun unsafeCall(
        stack: HostStack,
        references: ReferenceSlots,
    ): ChasmResult<Unit, InvocationError<ExceptionReference>> {
        return lane.unsafeCall(stack, references.objects)
    }
}

@ExperimentalChasmApi
fun prepareCall(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<PreparedCall, ExecutionError.Rejected> {
    return when (val prepared = _prepareObjectCall(store, instance, name)) {
        is ChasmResult.Success -> ChasmResult.Success(PreparedCall(prepared.result))
        is ChasmResult.Error -> prepared
    }
}
