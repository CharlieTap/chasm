package io.github.charlietap.chasm.decoder.wasm.fixture

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import kotlinx.io.IOException

fun ioError() = Err(WasmDecodeError.IOError(IOException()))
