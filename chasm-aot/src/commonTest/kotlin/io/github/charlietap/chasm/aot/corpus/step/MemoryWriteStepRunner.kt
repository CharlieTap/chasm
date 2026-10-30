package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.stringOrDefault
import io.github.charlietap.chasm.aot.corpus.value.decodeBytes
import io.github.charlietap.chasm.aot.corpus.value.encodeValueBytes
import io.github.charlietap.chasm.aot.corpus.value.parseIndex
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.shapes.fold
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

    return writeBytes(context.setup.store, memory, offset, bytes).fold(
        { CorpusResult.Success },
        { error -> context.failure("memory write failed", error.toString()) },
    )
}
