package io.github.charlietap.chasm.decoder.wasm.decoder.type.global

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryGlobalTypeDecoder(
    reader: WasmBinaryReader,
): Result<GlobalType, WasmDecodeError> = BinaryGlobalTypeDecoder(
    reader = reader,
    valueTypeDecoder = ::BinaryValueTypeDecoder,
)

internal fun BinaryGlobalTypeDecoder(
    reader: WasmBinaryReader,
    valueTypeDecoder: ValueTypeDecoder,
): Result<GlobalType, WasmDecodeError> = binding {
    val valueType = valueTypeDecoder(reader).bind()
    val mutability = when (val byte = reader.ubyte().bind()) {
        0u.toUByte() -> Ok(GlobalType.Mutability.Const)
        1u.toUByte() -> Ok(GlobalType.Mutability.Var)
        else -> Err(TypeDecodeError.UnknownMutabilityFlag(byte))
    }.bind()
    GlobalType(valueType, mutability)
}
