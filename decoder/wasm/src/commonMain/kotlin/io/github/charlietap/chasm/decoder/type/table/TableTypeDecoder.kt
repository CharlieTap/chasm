package io.github.charlietap.chasm.decoder.type.table

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias TableTypeDecoder = (WasmBinaryReader) -> Result<TableType, WasmDecodeError>
