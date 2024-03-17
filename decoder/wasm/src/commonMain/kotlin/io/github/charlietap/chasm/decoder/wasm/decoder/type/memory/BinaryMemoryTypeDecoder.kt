package io.github.charlietap.chasm.decoder.wasm.decoder.type.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.limits.BinaryLimitsDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.limits.LimitsDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryMemoryTypeDecoder(
    reader: WasmBinaryReader,
): Result<MemoryType, WasmDecodeError> = BinaryMemoryTypeDecoder(
    reader = reader,
    limitsDecoder = ::BinaryLimitsDecoder,
)

internal fun BinaryMemoryTypeDecoder(
    reader: WasmBinaryReader,
    limitsDecoder: LimitsDecoder,
): Result<MemoryType, WasmDecodeError> = binding {
    val limits = limitsDecoder(reader).bind()
    MemoryType(limits)
}
