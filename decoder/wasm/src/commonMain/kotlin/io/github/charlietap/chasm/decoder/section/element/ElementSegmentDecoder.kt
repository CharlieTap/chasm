package io.github.charlietap.chasm.decoder.section.element

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ElementSegmentDecoder = (WasmBinaryReader, Index.ElementIndex) -> Result<ElementSegment, WasmDecodeError>
