package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.AnyConvertExternDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitDataDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayInitElementDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayLenDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDataDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDefaultDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewElementDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewFixedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArraySetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ExternConvertAnyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.I31GetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.I31GetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.RefI31Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructNewDefaultDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructNewDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.StructSetDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.AnyConvertExtern
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayCopy
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayFill
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayGet
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayGetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayGetUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayInitData
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayInitElement
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayLen
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayNew
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayNewData
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayNewDefault
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayNewElement
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArrayNewFixed
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ArraySet
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.ExternConvertAny
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.I31GetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.I31GetUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.RefI31
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.StructGet
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.StructGetSigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.StructGetUnsigned
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.StructNew
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.StructNewDefault
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction.StructSet
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.dataAddress
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.type.ir.ext.arrayType
import io.github.charlietap.chasm.type.ir.ext.bitWidth
import io.github.charlietap.chasm.type.ir.ext.structType

internal fun AggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    AggregateInstructionPredecoder(
        context = context,
        instruction = instruction,
        anyConvertExternDispatcher = ::AnyConvertExternDispatcher,
        arrayCopyDispatcher = ::ArrayCopyDispatcher,
        arrayFillDispatcher = ::ArrayFillDispatcher,
        arrayGetDispatcher = ::ArrayGetDispatcher,
        arrayGetSignedDispatcher = ::ArrayGetSignedDispatcher,
        arrayGetUnsignedDispatcher = ::ArrayGetUnsignedDispatcher,
        arrayInitDataDispatcher = ::ArrayInitDataDispatcher,
        arrayInitElementDispatcher = ::ArrayInitElementDispatcher,
        arrayLenDispatcher = ::ArrayLenDispatcher,
        arrayNewDispatcher = ::ArrayNewDispatcher,
        arrayNewDataDispatcher = ::ArrayNewDataDispatcher,
        arrayNewDefaultDispatcher = ::ArrayNewDefaultDispatcher,
        arrayNewElementDispatcher = ::ArrayNewElementDispatcher,
        arrayNewFixedDispatcher = ::ArrayNewFixedDispatcher,
        arraySetDispatcher = ::ArraySetDispatcher,
        externConvertAnyDispatcher = ::ExternConvertAnyDispatcher,
        i31GetSignedDispatcher = ::I31GetSignedDispatcher,
        i31GetUnsignedDispatcher = ::I31GetUnsignedDispatcher,
        refI31Dispatcher = ::RefI31Dispatcher,
        structGetDispatcher = ::StructGetDispatcher,
        structGetSignedDispatcher = ::StructGetSignedDispatcher,
        structGetUnsignedDispatcher = ::StructGetUnsignedDispatcher,
        structNewDispatcher = ::StructNewDispatcher,
        structNewDefaultDispatcher = ::StructNewDefaultDispatcher,
        structSetDispatcher = ::StructSetDispatcher,
    )

internal inline fun AggregateInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction,
    crossinline anyConvertExternDispatcher: Dispatcher<AnyConvertExtern>,
    crossinline arrayCopyDispatcher: Dispatcher<ArrayCopy>,
    crossinline arrayFillDispatcher: Dispatcher<ArrayFill>,
    crossinline arrayGetDispatcher: Dispatcher<ArrayGet>,
    crossinline arrayGetSignedDispatcher: Dispatcher<ArrayGetSigned>,
    crossinline arrayGetUnsignedDispatcher: Dispatcher<ArrayGetUnsigned>,
    crossinline arrayInitDataDispatcher: Dispatcher<ArrayInitData>,
    crossinline arrayInitElementDispatcher: Dispatcher<ArrayInitElement>,
    crossinline arrayLenDispatcher: Dispatcher<ArrayLen>,
    crossinline arrayNewDispatcher: Dispatcher<ArrayNew>,
    crossinline arrayNewDataDispatcher: Dispatcher<ArrayNewData>,
    crossinline arrayNewDefaultDispatcher: Dispatcher<ArrayNewDefault>,
    crossinline arrayNewElementDispatcher: Dispatcher<ArrayNewElement>,
    crossinline arrayNewFixedDispatcher: Dispatcher<ArrayNewFixed>,
    crossinline arraySetDispatcher: Dispatcher<ArraySet>,
    crossinline externConvertAnyDispatcher: Dispatcher<ExternConvertAny>,
    crossinline i31GetSignedDispatcher: Dispatcher<I31GetSigned>,
    crossinline i31GetUnsignedDispatcher: Dispatcher<I31GetUnsigned>,
    crossinline refI31Dispatcher: Dispatcher<RefI31>,
    crossinline structGetDispatcher: Dispatcher<StructGet>,
    crossinline structGetSignedDispatcher: Dispatcher<StructGetSigned>,
    crossinline structGetUnsignedDispatcher: Dispatcher<StructGetUnsigned>,
    crossinline structNewDispatcher: Dispatcher<StructNew>,
    crossinline structNewDefaultDispatcher: Dispatcher<StructNewDefault>,
    crossinline structSetDispatcher: Dispatcher<StructSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AggregateInstruction.AnyConvertExtern -> anyConvertExternDispatcher(AnyConvertExtern)
        is AggregateInstruction.ArrayCopy -> arrayCopyDispatcher(ArrayCopy(instruction.sourceTypeIndex, instruction.destinationTypeIndex))
        is AggregateInstruction.ArrayFill -> arrayFillDispatcher(ArrayFill(instruction.typeIndex))
        is AggregateInstruction.ArrayGet -> arrayGetDispatcher(ArrayGet(instruction.typeIndex))
        is AggregateInstruction.ArrayGetSigned -> arrayGetSignedDispatcher(ArrayGetSigned(instruction.typeIndex))
        is AggregateInstruction.ArrayGetUnsigned -> arrayGetUnsignedDispatcher(ArrayGetUnsigned(instruction.typeIndex))
        is AggregateInstruction.ArrayInitData -> {
            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()
            val dataAddress = context.instance.dataAddress(instruction.dataIndex).bind()
            val dataInstance = context.store.data(dataAddress)
            val fieldWidthInBytes = arrayType.fieldType.bitWidth()?.let { sizeInBits ->
                sizeInBits / 8
            } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

            arrayInitDataDispatcher(ArrayInitData(instruction.typeIndex, dataInstance, fieldWidthInBytes))
        }
        is AggregateInstruction.ArrayInitElement -> {
            val elementAddress = context.instance.elementAddress(instruction.elementIndex).bind()
            val elementInstance = context.store.element(elementAddress)
            arrayInitElementDispatcher(
                ArrayInitElement(instruction.typeIndex, elementInstance),
            )
        }
        is AggregateInstruction.ArrayLen -> arrayLenDispatcher(ArrayLen)
        is AggregateInstruction.ArrayNew -> {
            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()

            arrayNewDispatcher(
                ArrayNew(definedType, arrayType),
            )
        }
        is AggregateInstruction.ArrayNewData -> {
            val dataAddress = context.instance.dataAddress(instruction.dataIndex).bind()
            val dataInstance = context.store.data(dataAddress)
            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()
            val fieldWidthInBytes = arrayType.fieldType.bitWidth()?.let { sizeInBits ->
                sizeInBits / 8
            } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

            arrayNewDataDispatcher(
                ArrayNewData(definedType, arrayType, dataInstance, fieldWidthInBytes),
            )
        }
        is AggregateInstruction.ArrayNewDefault -> {
            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()
            val field = arrayType.fieldType.default(context)

            arrayNewDefaultDispatcher(
                ArrayNewDefault(definedType, arrayType, field),
            )
        }
        is AggregateInstruction.ArrayNewElement -> {
            val elementAddress = context.instance.elementAddress(instruction.elementIndex).bind()
            val elementInstance = context.store.element(elementAddress)
            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()

            arrayNewElementDispatcher(
                ArrayNewElement(definedType, arrayType, elementInstance),
            )
        }
        is AggregateInstruction.ArrayNewFixed -> {
            val definedType = context.types[instruction.typeIndex.idx]
            val arrayType = context.unroller(definedType).compositeType.arrayType() ?: Err(
                InvocationError.ArrayCompositeTypeExpected,
            ).bind()

            arrayNewFixedDispatcher(ArrayNewFixed(definedType, arrayType, instruction.size))
        }
        is AggregateInstruction.ArraySet -> arraySetDispatcher(ArraySet(instruction.typeIndex))
        is AggregateInstruction.ExternConvertAny -> externConvertAnyDispatcher(ExternConvertAny)
        is AggregateInstruction.I31GetSigned -> i31GetSignedDispatcher(I31GetSigned)
        is AggregateInstruction.I31GetUnsigned -> i31GetUnsignedDispatcher(I31GetUnsigned)
        is AggregateInstruction.RefI31 -> refI31Dispatcher(RefI31)
        is AggregateInstruction.StructGet -> structGetDispatcher(StructGet(instruction.typeIndex, instruction.fieldIndex))
        is AggregateInstruction.StructGetSigned -> structGetSignedDispatcher(StructGetSigned(instruction.typeIndex, instruction.fieldIndex))
        is AggregateInstruction.StructGetUnsigned -> structGetUnsignedDispatcher(
            StructGetUnsigned(instruction.typeIndex, instruction.fieldIndex),
        )
        is AggregateInstruction.StructNew -> {
            val definedType = context.types[instruction.typeIndex.idx]
            val structType = context.unroller(definedType).compositeType.structType() ?: Err(
                InvocationError.StructCompositeTypeExpected,
            ).bind()

            structNewDispatcher(StructNew(definedType, structType))
        }
        is AggregateInstruction.StructNewDefault -> {
            val definedType = context.types[instruction.typeIndex.idx]
            val structType = context.unroller(definedType).compositeType.structType() ?: Err(
                InvocationError.StructCompositeTypeExpected,
            ).bind()
            val fields = LongArray(structType.fields.size) { idx ->
                structType.fields[idx].default(context)
            }

            structNewDefaultDispatcher(StructNewDefault(definedType, structType, fields))
        }
        is AggregateInstruction.StructSet -> {
            structSetDispatcher(StructSet(instruction.fieldIndex.idx))
        }
    }
}
