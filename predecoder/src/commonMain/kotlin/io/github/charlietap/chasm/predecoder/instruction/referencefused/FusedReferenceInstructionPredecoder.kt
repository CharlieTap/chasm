package io.github.charlietap.chasm.predecoder.instruction.referencefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefEqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefIsNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefTestDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction.RefCast
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction.RefEq
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction.RefIsNull
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction.RefNull
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction.RefTest
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ConcreteHeapType

internal fun FusedReferenceInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedReferenceInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        refCastDispatcher = ::RefCastDispatcher,
        refEqDispatcher = ::RefEqDispatcher,
        refIsNullDispatcher = ::RefIsNullDispatcher,
        refNullDispatcher = ::RefNullDispatcher,
        refTestDispatcher = ::RefTestDispatcher,
    )

internal inline fun FusedReferenceInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline refCastDispatcher: Dispatcher<RefCast>,
    crossinline refEqDispatcher: Dispatcher<RefEq>,
    crossinline refIsNullDispatcher: Dispatcher<RefIsNull>,
    crossinline refNullDispatcher: Dispatcher<RefNull>,
    crossinline refTestDispatcher: Dispatcher<RefTest>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedReferenceInstruction.RefEq -> {

            val reference1 = loadFactory(context, instruction.reference1)
            val reference2 = loadFactory(context, instruction.reference2)
            val destination = storeFactory(context, instruction.destination)

            refEqDispatcher(
                RefEq(
                    reference1 = reference1,
                    reference2 = reference2,
                    destination = destination,
                ),
            )
        }
        is FusedReferenceInstruction.RefIsNull -> {
            val value = loadFactory(context, instruction.value)
            val destination = storeFactory(context, instruction.destination)

            refIsNullDispatcher(
                RefIsNull(
                    value = value,
                    destination = destination,
                ),
            )
        }
        is FusedReferenceInstruction.RefNull -> {
            val destination = storeFactory(context, instruction.destination)
            val reference = ReferenceValue.Null(instruction.type).toLong()
            refNullDispatcher(RefNull(destination, reference))
        }
        is FusedReferenceInstruction.RefTest -> {
            val reference = loadFactory(context, instruction.reference)
            val destination = storeFactory(context, instruction.destination)

            // Pre resolve supertypes
            when (val heapType = instruction.referenceType.heapType) {
                is ConcreteHeapType.TypeIndex -> context.instance.runtimeTypes[heapType.index].hydrate()
                else -> Unit
            }

            refTestDispatcher(
                RefTest(
                    reference = reference,
                    destination = destination,
                    referenceType = instruction.referenceType,
                ),
            )
        }
        is FusedReferenceInstruction.RefCast -> {
            val reference = loadFactory(context, instruction.reference)
            val destination = storeFactory(context, instruction.destination)

            // Pre resolve supertypes
            when (val heapType = instruction.referenceType.heapType) {
                is ConcreteHeapType.TypeIndex -> context.instance.runtimeTypes[heapType.index].hydrate()
                else -> Unit
            }

            refCastDispatcher(
                RefCast(
                    reference = reference,
                    destination = destination,
                    referenceType = instruction.referenceType,
                ),
            )
        }
    }
}
