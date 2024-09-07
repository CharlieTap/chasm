package io.github.charlietap.chasm.decoder.fixture

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

fun ioError() = Err(WasmDecodeError.IOError(Exception()))
