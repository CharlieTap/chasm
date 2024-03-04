package io.github.charlietap.chasm.decoder.section.data

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias DataSegmentDecoder = (WasmBinaryReader, Index.DataIndex) -> Result<DataSegment, WasmDecodeError>
