package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayCopyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayFillDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayLenDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewFixedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArraySetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructNewDefaultDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructNewDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructSetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayCopy
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayFill
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayGet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayGetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayGetUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayLen
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayNew
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArrayNewFixed
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.ArraySet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructGet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructGetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructGetUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructNew
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructNewDefault
import io.github.charlietap.chasm.executor.runtime.instruction.FusedAggregateInstruction.StructSet
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType

internal fun FusedAggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedAggregateInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
        arrayCopyDispatcher = ::ArrayCopyDispatcher,
        arrayFillDispatcher = ::ArrayFillDispatcher,
        arrayGetDispatcher = ::ArrayGetDispatcher,
        arrayGetSignedDispatcher = ::ArrayGetSignedDispatcher,
        arrayGetUnsignedDispatcher = ::ArrayGetUnsignedDispatcher,
        arrayLenDispatcher = ::ArrayLenDispatcher,
        arrayNewDispatcher = ::ArrayNewDispatcher,
        arrayNewFixedDispatcher = ::ArrayNewFixedDispatcher,
        arraySetDispatcher = ::ArraySetDispatcher,
        structGetDispatcher = ::StructGetDispatcher,
        structGetSignedDispatcher = ::StructGetSignedDispatcher,
        structGetUnsignedDispatcher = ::StructGetUnsignedDispatcher,
        structNewDispatcher = ::StructNewDispatcher,
        structNewDefaultDispatcher = ::StructNewDefaultDispatcher,
        structSetDispatcher = ::StructSetDispatcher,
    )

internal inline fun FusedAggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedAggregateInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
    crossinline arrayCopyDispatcher: Dispatcher<ArrayCopy>,
    crossinline arrayFillDispatcher: Dispatcher<ArrayFill>,
    crossinline arrayGetDispatcher: Dispatcher<ArrayGet>,
    crossinline arrayGetSignedDispatcher: Dispatcher<ArrayGetSigned>,
    crossinline arrayGetUnsignedDispatcher: Dispatcher<ArrayGetUnsigned>,
    crossinline arrayLenDispatcher: Dispatcher<ArrayLen>,
    crossinline arrayNewDispatcher: Dispatcher<ArrayNew>,
    crossinline arrayNewFixedDispatcher: Dispatcher<ArrayNewFixed>,
    crossinline arraySetDispatcher: Dispatcher<ArraySet>,
    crossinline structGetDispatcher: Dispatcher<StructGet>,
    crossinline structGetSignedDispatcher: Dispatcher<StructGetSigned>,
    crossinline structGetUnsignedDispatcher: Dispatcher<StructGetUnsigned>,
    crossinline structNewDispatcher: Dispatcher<StructNew>,
    crossinline structNewDefaultDispatcher: Dispatcher<StructNewDefault>,
    crossinline structSetDispatcher: Dispatcher<StructSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedAggregateInstruction.ArrayCopy -> {

            val elementsToCopy = loadFactory(context, instruction.elementsToCopy)
            val sourceOffset = loadFactory(context, instruction.sourceOffset)
            val sourceAddress = loadFactory(context, instruction.sourceAddress)
            val destinationOffset = loadFactory(context, instruction.destinationOffset)
            val destinationAddress = loadFactory(context, instruction.destinationAddress)

            arrayCopyDispatcher(
                ArrayCopy(
                    elementsToCopy = elementsToCopy,
                    sourceOffset = sourceOffset,
                    sourceAddress = sourceAddress,
                    destinationOffset = destinationOffset,
                    destinationAddress = destinationAddress,
                    sourceTypeIndex = instruction.sourceTypeIndex,
                    destinationTypeIndex = instruction.destinationTypeIndex,
                ),
            )
        }
        is FusedAggregateInstruction.ArrayFill -> {

            val elementsToFill = loadFactory(context, instruction.elementsToFill)
            val fillValue = loadFactory(context, instruction.fillValue)
            val arrayElementOffset = loadFactory(context, instruction.arrayElementOffset)
            val address = loadFactory(context, instruction.address)

            arrayFillDispatcher(
                ArrayFill(
                    elementsToFill = elementsToFill,
                    fillValue = fillValue,
                    arrayElementOffset = arrayElementOffset,
                    address = address,
                    typeIndex = instruction.typeIndex,
                ),
            )
        }
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
        is FusedAggregateInstruction.ArrayLen -> {

            val address = loadFactory(context, instruction.address)
            val destination = storeFactory(context, instruction.destination)

            arrayLenDispatcher(
                ArrayLen(
                    address = address,
                    destination = destination,
                ),
            )
        }
        is FusedAggregateInstruction.ArrayNew -> {

            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()

            val size = loadFactory(context, instruction.size)
            val value = loadFactory(context, instruction.value)
            val destination = storeFactory(context, instruction.destination)

            arrayNewDispatcher(
                ArrayNew(
                    size = size,
                    value = value,
                    destination = destination,
                    definedType = definedType,
                    arrayType = arrayType,
                ),
            )
        }
        is FusedAggregateInstruction.ArrayNewFixed -> {

            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()

            val destination = storeFactory(context, instruction.destination)

            arrayNewFixedDispatcher(
                ArrayNewFixed(
                    destination = destination,
                    definedType = definedType,
                    arrayType = arrayType,
                    size = instruction.size,
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
        is FusedAggregateInstruction.StructNew -> {
            val destination = storeFactory(context, instruction.destination)
            val definedType = context.types[instruction.typeIndex.idx]
            val structType = context.unroller(definedType).compositeType.structType() ?: Err(
                InvocationError.StructCompositeTypeExpected,
            ).bind()

            structNewDispatcher(
                StructNew(
                    destination = destination,
                    structType = structType,
                    definedType = definedType,
                ),
            )
        }
        is FusedAggregateInstruction.StructNewDefault -> {
            val destination = storeFactory(context, instruction.destination)
            val definedType = context.types[instruction.typeIndex.idx]
            val structType = context.unroller(definedType).compositeType.structType() ?: Err(
                InvocationError.StructCompositeTypeExpected,
            ).bind()
            val fields = LongArray(structType.fields.size) { idx ->
                structType.fields[idx].default(context)
            }

            structNewDefaultDispatcher(
                StructNewDefault(
                    destination = destination,
                    definedType = definedType,
                    structType = structType,
                    fields = fields,
                ),
            )
        }
        is FusedAggregateInstruction.StructSet -> {

            val value = loadFactory(context, instruction.value)
            val address = loadFactory(context, instruction.address)

            structSetDispatcher(
                StructSet(
                    value = value,
                    address = address,
                    fieldIndex = instruction.fieldIndex.idx,
                ),
            )
        }
    }
}
