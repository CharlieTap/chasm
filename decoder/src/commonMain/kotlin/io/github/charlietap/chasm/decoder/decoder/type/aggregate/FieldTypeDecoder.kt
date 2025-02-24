package io.github.charlietap.chasm.decoder.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.StorageType

internal fun FieldTypeDecoder(
    context: DecoderContext,
): Result<FieldType, WasmDecodeError> =
    FieldTypeDecoder(
        context = context,
        storageTypeDecoder = ::StorageTypeDecoder,
        mutabilityDecoder = ::MutabilityDecoder,
    )

internal inline fun FieldTypeDecoder(
    context: DecoderContext,
    crossinline storageTypeDecoder: Decoder<StorageType>,
    crossinline mutabilityDecoder: Decoder<Mutability>,
): Result<FieldType, WasmDecodeError> = binding {
    val storageType = storageTypeDecoder(context).bind()
    val mutability = mutabilityDecoder(context).bind()

    FieldType(storageType, mutability)
}
