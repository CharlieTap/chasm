package io.github.charlietap.chasm.fixture

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.error.WasmDecodeError
import kotlinx.io.IOException

fun ioError() = Err(WasmDecodeError.IOError(IOException()))
