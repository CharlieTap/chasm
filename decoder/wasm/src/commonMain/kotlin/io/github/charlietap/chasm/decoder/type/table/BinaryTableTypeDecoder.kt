package io.github.charlietap.chasm.decoder.type.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.type.limits.BinaryLimitsDecoder
import io.github.charlietap.chasm.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.type.reference.BinaryReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryTableTypeDecoder(
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
    val referenceType = referenceTypeDecoder(referenceTypeByte).bind()
    val limits = limitsDecoder(reader).bind()
    TableType(referenceType, limits)
}
