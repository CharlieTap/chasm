package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.aot.corpus.value.decodeBytes
import io.github.charlietap.chasm.aot.corpus.value.toHex
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal typealias HostEffectsVerifier = (StepContext, JsonObject) -> CorpusResult.Failure?

internal fun HostEffectsVerifier(
    context: StepContext,
    hostEffects: JsonObject,
): CorpusResult.Failure? {
    val imports = context.setup.imports

    hostEffects["stdout"]?.let { expected ->
        verifyOutput(context, "stdout mismatch", decodeBytes(expected), imports.stdout.toByteArray())?.let { return it }
    }

    hostEffects["stderr"]?.let { expected ->
        verifyOutput(context, "stderr mismatch", decodeBytes(expected), imports.stderr.toByteArray())?.let { return it }
    }

    hostEffects["import_captures"]?.jsonArray?.forEach { element ->
        val assertion = element.jsonObject
        val name = assertion.string("name")
        val capture = imports.captures[name]
            ?: return context.failure("missing import capture", expected = name)
        val expected = assertion["text"]?.jsonPrimitive?.contentOrNull?.encodeToByteArray()
            ?: assertion["bytes"]?.let(::decodeBytes)
            ?: return context.failure("import capture requires text or bytes", name)
        val actual = capture.text.toString().encodeToByteArray()
        if (!expected.contentEquals(actual)) {
            return context.failure("import capture mismatch", detail = name, expected = expected.toHex(), actual = actual.toHex())
        }
    }

    return null
}

private fun verifyOutput(
    context: StepContext,
    message: String,
    expected: ByteArray,
    actual: ByteArray,
): CorpusResult.Failure? = if (expected.contentEquals(actual)) {
    null
} else {
    context.failure(message, expected = expected.toHex(), actual = actual.toHex())
}
