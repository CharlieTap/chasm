package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryMemArgDecoder(
    reader: WasmBinaryReader,
): Result<MemArg, WasmDecodeError> = binding {
    MemArg(reader.uint().bind(), reader.uint().bind())
}
