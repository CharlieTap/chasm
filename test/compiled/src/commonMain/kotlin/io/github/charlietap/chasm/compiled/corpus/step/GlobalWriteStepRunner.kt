package io.github.charlietap.chasm.compiled.corpus.step

import io.github.charlietap.chasm.compiled.corpus.ext.string
import io.github.charlietap.chasm.compiled.corpus.value.bits
import io.github.charlietap.chasm.compiled.corpus.value.parseValue
import io.github.charlietap.chasm.compiled.corpus.value.reference
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal typealias GlobalWriteStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun GlobalWriteStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val name = step.string("global")
    val global = context.setup.global(name) ?: return context.failure("global not found", name)
    val value = parseValue(step.getValue("value").jsonObject, context.captures)

    return try {
        when (value) {
            is NumberValue<*> -> {
                val bits = value.bits()
                context.timings.execute { writeGlobal(global, bits) }
            }
            is ReferenceValue -> {
                val reference = value.reference()
                context.timings.execute { writeGlobalReference(global, reference) }
            }
            is VectorValue -> return context.failure("unsupported global value", value.toString())
        }
        CorpusResult.Success
    } catch (failure: IllegalArgumentException) {
        context.failure("global write failed", failure.message)
    }
}
