package io.github.charlietap.chasm.decoder.decoder.type.value

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.heap.HEAP_TYPE_NO_FUNC
import io.github.charlietap.chasm.decoder.decoder.type.number.NUMBER_TYPE_F64
import io.github.charlietap.chasm.decoder.decoder.type.number.NUMBER_TYPE_I32
import io.github.charlietap.chasm.decoder.decoder.type.number.NumberTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.reference.REFERENCE_TYPE_REF_NULL
import io.github.charlietap.chasm.decoder.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.decoder.type.vector.VECTOR_TYPE_128
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType

internal fun ValueTypeDecoder(
    context: DecoderContext,
): Result<ValueType, WasmDecodeError> =
    ValueTypeDecoder(
        context = context,
        numberTypeDecoder = ::NumberTypeDecoder,
        referenceTypeDecoder = ::ReferenceTypeDecoder,
    )

internal inline fun ValueTypeDecoder(
    context: DecoderContext,
    crossinline numberTypeDecoder: Decoder<NumberType>,
    crossinline referenceTypeDecoder: Decoder<ReferenceType>,
): Result<ValueType, WasmDecodeError> = binding {
    when (
        val byte = context.reader
            .peek()
            .ubyte()
            .bind()
    ) {
        in NUMBER_TYPE_RANGE -> {
            val numberType = numberTypeDecoder(context).bind()
            ValueType.Number(numberType)
        }
        in VECTOR_TYPE_RANGE -> {
            context.reader.ubyte().bind() // consume byte
            ValueType.Vector(VectorType.V128)
        }
        in REFERENCE_TYPE_RANGE -> {
            val referenceType = referenceTypeDecoder(context).bind()
            ValueType.Reference(referenceType)
        }
        else -> Err(TypeDecodeError.InvalidValueType(byte)).bind<ValueType>()
    }
}

internal val NUMBER_TYPE_RANGE = NUMBER_TYPE_F64..NUMBER_TYPE_I32
internal val VECTOR_TYPE_RANGE = VECTOR_TYPE_128..VECTOR_TYPE_128
internal val REFERENCE_TYPE_RANGE = REFERENCE_TYPE_REF_NULL..HEAP_TYPE_NO_FUNC
