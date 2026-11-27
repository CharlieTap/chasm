package io.github.charlietap.chasm.decoder.fixture

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.annotation.UnsafeResultErrorAccess
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal fun ioError() = Err(WasmDecodeError.IOError(Exception()))

@OptIn(UnsafeResultErrorAccess::class)
internal inline fun assertWasmDecodeError(
    expected: Result<Nothing, WasmDecodeError>,
    block: () -> Any?,
) {
    val actual = assertFailsWith<WasmDecodeException> { block() }
    assertEquals(expected.error, actual.error)
}

internal inline fun assertWasmDecodeError(
    expected: WasmDecodeError,
    block: () -> Any?,
) {
    val actual = assertFailsWith<WasmDecodeException> { block() }
    assertEquals(expected, actual.error)
}
