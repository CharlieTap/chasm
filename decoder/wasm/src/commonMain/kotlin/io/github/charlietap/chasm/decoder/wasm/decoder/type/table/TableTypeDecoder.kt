package io.github.charlietap.chasm.decoder.wasm.decoder.type.table

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias TableTypeDecoder = (WasmBinaryReader) -> Result<TableType, WasmDecodeError>
