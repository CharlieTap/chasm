package io.github.charlietap.chasm.decoder.wasm.decoder.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.decoder.wasm.error.TypeDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryMutabilityDecoder(
    reader: WasmBinaryReader,
): Result<Mutability, WasmDecodeError> = binding {
    when (val byte = reader.ubyte().bind()) {
        CONST_MUTABILITY -> Ok(Mutability.Const)
        VAR_MUTABILITY -> Ok(Mutability.Var)
        else -> Err(TypeDecodeError.UnknownMutabilityFlag(byte))
    }.bind()
}

internal const val CONST_MUTABILITY: UByte = 0x00u
internal const val VAR_MUTABILITY: UByte = 0x01u
