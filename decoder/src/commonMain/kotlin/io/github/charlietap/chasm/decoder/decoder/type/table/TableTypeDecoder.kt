package io.github.charlietap.chasm.decoder.decoder.type.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.TableType

internal fun TableTypeDecoder(
    context: DecoderContext,
): Result<TableType, WasmDecodeError> = TableTypeDecoder(
    context = context,
    referenceTypeDecoder = ::ReferenceTypeDecoder,
    limitsDecoder = ::LimitsDecoder,
)

internal inline fun TableTypeDecoder(
    context: DecoderContext,
    crossinline referenceTypeDecoder: Decoder<ReferenceType>,
    crossinline limitsDecoder: Decoder<Pair<Limits, SharedStatus>>,
): Result<TableType, WasmDecodeError> = binding {
    val referenceType = referenceTypeDecoder(context).bind()
    val (limits, _) = limitsDecoder(context).bind()
    TableType(referenceType, limits)
}
