package io.github.charlietap.chasm.decoder.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

internal fun BinaryMemArgDecoder(
    reader: WasmBinaryReader,
): Result<MemArg, WasmDecodeError> = binding {
    MemArg(reader.uint().bind(), reader.uint().bind())
}
