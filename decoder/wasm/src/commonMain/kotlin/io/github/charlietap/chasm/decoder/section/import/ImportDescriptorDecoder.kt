package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias ImportDescriptorDecoder = (WasmBinaryReader) -> Result<Import.Descriptor, WasmDecodeError>
