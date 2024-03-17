package io.github.charlietap.chasm.decoder.wasm.decoder.type.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.limits.BinaryLimitsDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryTableTypeDecoder(
    reader: WasmBinaryReader,
): Result<TableType, WasmDecodeError> = BinaryTableTypeDecoder(
    reader = reader,
    referenceTypeDecoder = ::BinaryReferenceTypeDecoder,
    limitsDecoder = ::BinaryLimitsDecoder,
)

internal fun BinaryTableTypeDecoder(
    reader: WasmBinaryReader,
    referenceTypeDecoder: ReferenceTypeDecoder,
    limitsDecoder: LimitsDecoder,
): Result<TableType, WasmDecodeError> = binding {
    val referenceTypeByte = reader.ubyte().bind()
    val referenceType = referenceTypeDecoder(reader, referenceTypeByte).bind()
    val limits = limitsDecoder(reader).bind()
    TableType(referenceType, limits)
}
