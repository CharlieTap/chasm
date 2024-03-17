package io.github.charlietap.chasm.decoder.wasm.decoder.type.reference

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ReferenceTypeDecoder = (WasmBinaryReader, UByte) -> Result<ReferenceType, WasmDecodeError>
