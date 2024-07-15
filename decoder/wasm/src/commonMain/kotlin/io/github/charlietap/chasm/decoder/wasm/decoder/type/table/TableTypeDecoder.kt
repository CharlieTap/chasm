package io.github.charlietap.chasm.decoder.wasm.decoder.type.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.reference.ReferenceTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun TableTypeDecoder(
    context: DecoderContext,
): Result<TableType, WasmDecodeError> = TableTypeDecoder(
    context = context,
    referenceTypeDecoder = ::ReferenceTypeDecoder,
    limitsDecoder = ::LimitsDecoder,
)

internal fun TableTypeDecoder(
    context: DecoderContext,
    referenceTypeDecoder: Decoder<ReferenceType>,
    limitsDecoder: Decoder<Limits>,
): Result<TableType, WasmDecodeError> = binding {
    val referenceType = referenceTypeDecoder(context).bind()
    val limits = limitsDecoder(context).bind()
    TableType(referenceType, limits)
}
