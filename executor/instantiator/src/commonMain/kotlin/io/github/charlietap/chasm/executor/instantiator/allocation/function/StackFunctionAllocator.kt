package io.github.charlietap.chasm.executor.instantiator.allocation.function

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.StackFunctionCallDispatcher
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.factory.RTTFactory

typealias StackFunctionAllocator = (Store, FunctionType, StackFunctionBody) -> ExternalValue.Function

fun StackFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    body: StackFunctionBody,
): ExternalValue.Function =
    StackFunctionAllocator(
        store = store,
        functionType = functionType,
        body = body,
        callDispatcher = ::StackFunctionCallDispatcher,
        rttFactory = ::RTTFactory,
    )

fun StackFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    body: StackFunctionBody,
    callDispatcher: Dispatcher<ControlInstruction.StackFunctionCall>,
    rttFactory: RTTFactory,
): ExternalValue.Function {
    val type = functionType.definedType()
    val rtt = rttFactory(type, store.rttCache).apply {
        hydrate()
    }
    val instance = FunctionInstance.StackFunction(rtt, functionType, body)
    val instruction = callDispatcher(ControlInstruction.StackFunctionCall(instance))

    store.functions.add(instance)
    store.instructions.add(instruction)

    return ExternalValue.Function(Address.Function(store.functions.lastIndex))
}
