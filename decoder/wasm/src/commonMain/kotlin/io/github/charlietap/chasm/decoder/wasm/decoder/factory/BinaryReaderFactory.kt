package io.github.charlietap.chasm.decoder.wasm.decoder.factory

import io.github.charlietap.chasm.decoder.SourceReader
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias BinaryReaderFactory = (SourceReader) -> WasmBinaryReader
