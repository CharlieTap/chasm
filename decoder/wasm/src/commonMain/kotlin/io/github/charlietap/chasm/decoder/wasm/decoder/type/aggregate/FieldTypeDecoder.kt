package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun FieldTypeDecoder(
    context: DecoderContext,
): Result<FieldType, WasmDecodeError> =
    FieldTypeDecoder(
        context = context,
        storageTypeDecoder = ::StorageTypeDecoder,
        mutabilityDecoder = ::MutabilityDecoder,
    )

internal fun FieldTypeDecoder(
    context: DecoderContext,
    storageTypeDecoder: Decoder<StorageType>,
    mutabilityDecoder: Decoder<Mutability>,
): Result<FieldType, WasmDecodeError> = binding {
    val storageType = storageTypeDecoder(context).bind()
    val mutability = mutabilityDecoder(context).bind()

    FieldType(storageType, mutability)
}
