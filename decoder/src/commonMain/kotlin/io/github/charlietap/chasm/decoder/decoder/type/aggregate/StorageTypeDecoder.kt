package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.value.NUMBER_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.REFERENCE_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.VECTOR_TYPE_RANGE
import io.github.charlietap.chasm.decoder.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

internal fun StorageTypeDecoder(
    context: DecoderContext,
): Result<StorageType, WasmDecodeError> =
    StorageTypeDecoder(
        context = context,
        packedTypeDecoder = ::PackedTypeDecoder,
        valueTypeDecoder = ::ValueTypeDecoder,
    )

internal inline fun StorageTypeDecoder(
    context: DecoderContext,
    crossinline packedTypeDecoder: Decoder<PackedType>,
    crossinline valueTypeDecoder: Decoder<ValueType>,
): Result<StorageType, WasmDecodeError> = binding {
    when (
        val encoded = context.reader
            .peek()
            .ubyte()
            .bind()
    ) {
        in NUMBER_TYPE_RANGE,
        in VECTOR_TYPE_RANGE,
        in REFERENCE_TYPE_RANGE,
        -> {
            val valueType = valueTypeDecoder(context).bind()
            StorageType.Value(valueType)
        }
        in PACKED_TYPE_RANGE -> {
            val packedType = packedTypeDecoder(context).bind()
            StorageType.Packed(packedType)
        }
        else -> Err(TypeDecodeError.InvalidStorageType(encoded)).bind<StorageType>()
    }
}
