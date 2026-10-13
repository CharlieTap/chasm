package io.github.charlietap.chasm.decoder.parallel

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.decoder.reader.BufferedWasmBinaryReader

internal class DecodeFailure(
    val offset: Int,
    val error: WasmDecodeError,
)

/** Runs [block], returning its failure with the offset in the binary [reader] had reached. */
internal inline fun failureOf(
    reader: BufferedWasmBinaryReader,
    block: () -> Result<Unit, WasmDecodeError>,
): DecodeFailure? {
    val result = try {
        block()
    } catch (error: WasmDecodeException) {
        Err(error.error)
    } catch (error: Throwable) {
        Err(WasmDecodeError.IOError(error))
    }
    return result.fold(
        success = { null },
        failure = { error -> DecodeFailure(reader.position().toInt(), error) },
    )
}
