package io.github.charlietap.chasm.decoder.section.table

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize
import io.github.charlietap.chasm.section.TableSection

fun interface TableSectionDecoder : (WasmBinaryReader, SectionSize) -> Result<TableSection, WasmDecodeError>
