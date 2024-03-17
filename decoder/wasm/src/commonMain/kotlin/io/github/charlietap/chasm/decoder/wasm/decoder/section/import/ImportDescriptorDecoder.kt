package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ImportDescriptorDecoder = (WasmBinaryReader) -> Result<Import.Descriptor, WasmDecodeError>
