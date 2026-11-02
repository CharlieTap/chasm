package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.aot.corpus.ext.stringOrDefault
import io.github.charlietap.chasm.aot.corpus.value.decodeBytes
import io.github.charlietap.chasm.aot.corpus.value.encodeValueBytes
import io.github.charlietap.chasm.aot.corpus.value.parseIndex
import io.github.charlietap.chasm.aot.corpus.value.readLittleEndian
import io.github.charlietap.chasm.aot.corpus.value.toHex
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal typealias MemoryReadStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun MemoryReadStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val store = context.setup.store
    val memory = context.setup.memory(step.stringOrDefault("memory", "memory"))
        ?: return context.failure("memory not found", step["memory"]?.toString())
    val offset = parseIndex(step.getValue("offset"), context.captures)

    step["capture"]?.jsonObject?.let { capture ->
        val value = readValue(store, memory, offset, capture.string("type"))
            ?: return context.failure("memory capture failed")
        context.captures[capture.string("capture")] = value
        return CorpusResult.Success
    }

    val assertion = step["assert"]?.jsonObject
        ?: return context.failure("memory read requires assert or capture")
    val expected = assertion["bytes"]?.let(::decodeBytes)
        ?: assertion["value"]?.jsonObject?.let(::encodeValueBytes)
        ?: return context.failure("unsupported memory assertion")

    val actual = when (val result = readBytes(store, memory, ByteArray(expected.size), offset, expected.size)) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return context.failure("memory read failed", result.error.toString())
    }

    return if (expected.contentEquals(actual)) {
        CorpusResult.Success
    } else {
        context.failure("memory mismatch", expected = expected.toHex(), actual = actual.toHex())
    }
}

private fun readValue(
    store: Store,
    memory: Memory,
    offset: Int,
    type: String,
): ExecutionValue? {
    val size = if (type == "i64" || type == "f64") 8 else 4
    val bytes = when (val result = readBytes(store, memory, ByteArray(size), offset, size)) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return null
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
