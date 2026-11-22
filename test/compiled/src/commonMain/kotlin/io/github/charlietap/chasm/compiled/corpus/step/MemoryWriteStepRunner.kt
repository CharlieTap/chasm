package io.github.charlietap.chasm.compiled.corpus.step

import io.github.charlietap.chasm.compiled.corpus.ext.stringOrDefault
import io.github.charlietap.chasm.compiled.corpus.value.decodeBytes
import io.github.charlietap.chasm.compiled.corpus.value.encodeValueBytes
import io.github.charlietap.chasm.compiled.corpus.value.parseIndex
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal typealias MemoryWriteStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun MemoryWriteStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val memory = context.setup.memory(step.stringOrDefault("memory", "memory"))
        ?: return context.failure("memory not found", step["memory"]?.toString())
    val offset = parseIndex(step.getValue("offset"), context.captures)
    val bytes = step["bytes"]?.let(::decodeBytes)
        ?: step["value"]?.jsonObject?.let(::encodeValueBytes)
        ?: return context.failure("memory write requires bytes or value")

    return try {
        context.timings.execute { writeBytes(memory, offset, bytes) }
        CorpusResult.Success
    } catch (failure: IndexOutOfBoundsException) {
        context.failure("memory write failed", failure.message)
    }
}
