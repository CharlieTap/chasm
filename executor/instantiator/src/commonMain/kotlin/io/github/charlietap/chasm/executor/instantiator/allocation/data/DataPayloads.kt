package io.github.charlietap.chasm.executor.instantiator.allocation.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataReader
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

class DataPayloads(
    val payloads: Array<ByteArray?>,
    reader: DataReader?,
) {

    var reader: DataReader? = reader
        private set

    fun close() {
        payloads.fill(null)
        val open = reader ?: return
        reader = null
        try {
            open.close()
        } catch (_: Exception) {
        }
    }
}

internal typealias DataPayloadPreparer = (ModuleDeclarations) -> Result<DataPayloads, ModuleTrapError>

internal fun DataPayloadPreparer(
    module: ModuleDeclarations,
): Result<DataPayloads, ModuleTrapError> {
    val segments = module.dataSegments
    if (segments.none { segment -> segment.payload is DataPayload.External }) {
        return Ok(DataPayloads(Array(segments.size) { index -> (segments[index].payload as DataPayload.Inline).bytes }, null))
    }
    val source = module.dataSource ?: return Err(InstantiationError.DataUnavailable("the module has no data source"))
    val reader = attempt { source.open() }.let { opened -> opened.value ?: return Err(opened.error) }
    var owned = true
    try {
        val prepared = binding {
            val length = try {
                reader.length
            } catch (
                @Suppress("TooGenericExceptionCaught") thrown: Exception,
            ) {
                Err(InstantiationError.DataUnavailable(thrown.message ?: thrown.toString())).bind()
            }
            if (length != null) {
                segments.forEach { segment ->
                    val payload = segment.payload as? DataPayload.External ?: return@forEach
                    if (payload.offset < 0 || payload.size < 0 || payload.offset > length - payload.size) {
                        Err(InstantiationError.DataUnavailable("a payload at ${payload.offset} lies outside the $length bytes of data")).bind()
                    }
                }
            }
            Array(segments.size) { index ->
                val segment = segments[index]
                when (val payload = segment.payload) {
                    is DataPayload.Inline -> payload.bytes
                    is DataPayload.External -> if (length != null && segment.mode is DataSegmentDeclaration.Mode.Active) {
                        null
                    } else {
                        attempt { reader.read(payload.offset, payload.size) }.let { it.value ?: Err(it.error).bind() }
                    }
                }
            }
        }.getOrElse { error -> return Err(error) }
        if (prepared.any { bytes -> bytes == null }) {
            val holding = Ok(DataPayloads(prepared, reader))
            owned = false
            return holding
        }
        owned = false
        val closed = attempt { reader.close() }
        return if (closed.value == null) Err(closed.error) else Ok(DataPayloads(prepared, null))
    } finally {
        if (owned) attempt { reader.close() }
    }
}

private class Attempt<T>(
    val value: T?,
    val error: InstantiationError.DataUnavailable,
)

@Suppress("TooGenericExceptionCaught")
private inline fun <T : Any> attempt(
    block: () -> T,
): Attempt<T> = try {
    Attempt(block(), NONE)
} catch (thrown: Exception) {
    Attempt(null, InstantiationError.DataUnavailable(thrown.message ?: thrown.toString()))
}

private val NONE = InstantiationError.DataUnavailable("")
