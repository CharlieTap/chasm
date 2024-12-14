package io.github.charlietap.chasm.decoder.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal val MAGIC_NUMBER = ubyteArrayOf(0x00u, 0x61u, 0x73u, 0x6Du)

internal fun UByteArray.magicNumber() = if (contentEquals(MAGIC_NUMBER)) {
    Ok(Unit)
} else {
    Err(WasmDecodeError.InvalidWasmFile(this))
}

internal fun UByteArray.version() = Version.entries
    .firstOrNull { version ->
        contentEquals(version.number)
    }.toResultOr { WasmDecodeError.UnsupportedVersion(this) }
