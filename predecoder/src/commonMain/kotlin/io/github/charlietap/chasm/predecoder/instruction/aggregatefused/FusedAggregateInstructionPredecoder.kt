package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArraySetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayGet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayGetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayGetUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArraySet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructGet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructGetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructGetUnsigned
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory

internal fun FusedAggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedAggregateInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        arrayGetDispatcher = ::ArrayGetDispatcher,
        arrayGetSignedDispatcher = ::ArrayGetSignedDispatcher,
        arrayGetUnsignedDispatcher = ::ArrayGetUnsignedDispatcher,
        arraySetDispatcher = ::ArraySetDispatcher,
        structGetDispatcher = ::StructGetDispatcher,
        structGetSignedDispatcher = ::StructGetSignedDispatcher,
        structGetUnsignedDispatcher = ::StructGetUnsignedDispatcher,
    )

internal inline fun FusedAggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline arrayGetDispatcher: Dispatcher<ArrayGet>,
    crossinline arrayGetSignedDispatcher: Dispatcher<ArrayGetSigned>,
    crossinline arrayGetUnsignedDispatcher: Dispatcher<ArrayGetUnsigned>,
    crossinline arraySetDispatcher: Dispatcher<ArraySet>,
    crossinline structGetDispatcher: Dispatcher<StructGet>,
    crossinline structGetSignedDispatcher: Dispatcher<StructGetSigned>,
    crossinline structGetUnsignedDispatcher: Dispatcher<StructGetUnsigned>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedAggregateInstruction.ArrayGet -> {

            val field = loadFactory(context, instruction.field)
            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            arrayGetDispatcher(
                ArrayGet(
                    field = field,
                    address = address,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                ),
            )
        }
        is FusedAggregateInstruction.ArrayGetSigned -> {

            val field = loadFactory(context, instruction.field)
            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            arrayGetSignedDispatcher(
                ArrayGetSigned(
                    field = field,
                    address = address,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                ),
            )
        }
        is FusedAggregateInstruction.ArrayGetUnsigned -> {

            val field = loadFactory(context, instruction.field)
            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            arrayGetUnsignedDispatcher(
                ArrayGetUnsigned(
                    field = field,
                    address = address,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                ),
            )
        }
        is FusedAggregateInstruction.ArraySet -> {

            val value = loadFactory(context, instruction.value)
            val field = loadFactory(context, instruction.field)
            val address = loadFactory(context, instruction.address)

            arraySetDispatcher(
                ArraySet(
                    value = value,
                    field = field,
                    address = address,
                    typeIndex = instruction.typeIndex,
                ),
            )
        }
        is FusedAggregateInstruction.StructGet -> {

            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            structGetDispatcher(
                StructGet(
                    address = address,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                ),
            )
        }
        is FusedAggregateInstruction.StructGetSigned -> {

            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            structGetSignedDispatcher(
                StructGetSigned(
                    address = address,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                ),
            )
        }
        is FusedAggregateInstruction.StructGetUnsigned -> {

            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            structGetUnsignedDispatcher(
                StructGetUnsigned(
                    address = address,
                    destination = destination,
                    typeIndex = instruction.typeIndex,
                    fieldIndex = instruction.fieldIndex,
                ),
            )
        }
    }
}
