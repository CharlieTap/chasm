package io.github.charlietap.chasm.decoder.wasm.decoder.type.heap

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal typealias AbstractHeapTypeDecoder = (UByte) -> Result<AbstractHeapType, WasmDecodeError>
