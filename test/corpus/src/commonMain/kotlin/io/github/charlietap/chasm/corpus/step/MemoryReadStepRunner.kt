package io.github.charlietap.chasm.corpus.step

import io.github.charlietap.chasm.corpus.CorpusTimingRecorder
import io.github.charlietap.chasm.corpus.ext.string
import io.github.charlietap.chasm.corpus.ext.stringOrDefault
import io.github.charlietap.chasm.corpus.value.decodeBytes
import io.github.charlietap.chasm.corpus.value.encodeValueBytes
import io.github.charlietap.chasm.corpus.value.parseIndex
import io.github.charlietap.chasm.corpus.value.readLittleEndian
import io.github.charlietap.chasm.corpus.value.toHex
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal typealias MemoryReadStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun MemoryReadStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val memory = context.setup.memory(step.stringOrDefault("memory", "memory"))
        ?: return context.failure("memory not found", step["memory"]?.toString())
    val offset = parseIndex(step.getValue("offset"), context.captures)

    step["capture"]?.jsonObject?.let { capture ->
        val value = readValue(context.timings, memory, offset, capture.string("type"))
            ?: return context.failure("memory capture failed")
        context.captures[capture.string("capture")] = value
        return CorpusResult.Success
    }

    val assertion = step["assert"]?.jsonObject
        ?: return context.failure("memory read requires assert or capture")
    val expected = assertion["bytes"]?.let(::decodeBytes)
        ?: assertion["value"]?.jsonObject?.let(::encodeValueBytes)
        ?: return context.failure("unsupported memory assertion")

    val buffer = ByteArray(expected.size)
    val actual = try {
        context.timings.execute { readBytes(memory, buffer, offset, expected.size) }
    } catch (failure: IndexOutOfBoundsException) {
        return context.failure("memory read failed", failure.message)
    }

    return if (expected.contentEquals(actual)) {
        CorpusResult.Success
    } else {
        context.failure("memory mismatch", expected = expected.toHex(), actual = actual.toHex())
    }
}

private fun readValue(
    timings: CorpusTimingRecorder,
    memory: Memory,
    offset: Int,
    type: String,
): ExecutionValue? {
    val size = if (type == "i64" || type == "f64") 8 else 4
    val buffer = ByteArray(size)
    val bytes = try {
        timings.execute { readBytes(memory, buffer, offset, size) }
    } catch (_: IndexOutOfBoundsException) {
        return null
    }
    val bits = bytes.readLittleEndian()
    return when (type) {
        "i32" -> NumberValue.I32(bits.toInt())
        "i64" -> NumberValue.I64(bits)
        "f32" -> NumberValue.F32(Float.fromBits(bits.toInt()))
        "f64" -> NumberValue.F64(Double.fromBits(bits))
        else -> null
    }
}
