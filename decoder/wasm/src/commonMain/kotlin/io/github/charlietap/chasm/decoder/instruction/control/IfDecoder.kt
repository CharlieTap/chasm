package io.github.charlietap.chasm.decoder.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias IfDecoder = (WasmBinaryReader) -> Result<Pair<List<Instruction>, List<Instruction>?>, WasmDecodeError>
