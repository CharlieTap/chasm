package io.github.charlietap.chasm.decoder.wasm.decoder.type.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.BinaryMutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.MutabilityDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryGlobalTypeDecoder(
    reader: WasmBinaryReader,
): Result<GlobalType, WasmDecodeError> = BinaryGlobalTypeDecoder(
    reader = reader,
    valueTypeDecoder = ::BinaryValueTypeDecoder,
    mutabilityDecoder = ::BinaryMutabilityDecoder,
)

internal fun BinaryGlobalTypeDecoder(
    reader: WasmBinaryReader,
    valueTypeDecoder: ValueTypeDecoder,
    mutabilityDecoder: MutabilityDecoder,
): Result<GlobalType, WasmDecodeError> = binding {
    val valueType = valueTypeDecoder(reader).bind()
    val mutability = mutabilityDecoder(reader).bind()
    GlobalType(valueType, mutability)
}
