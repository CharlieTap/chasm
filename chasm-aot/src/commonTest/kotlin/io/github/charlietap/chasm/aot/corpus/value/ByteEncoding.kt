package io.github.charlietap.chasm.aot.corpus.value

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.corpus.lib.fixture.FixtureBytes
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun decodeBytes(
    value: JsonElement,
): ByteArray {
    val bytes = value.jsonObject["bytes"]?.jsonObject ?: value.jsonObject
    return decodeBytes(bytes.string("encoding"), bytes.string("value"))
}

internal fun decodeFixtureBytes(
    value: FixtureBytes,
): ByteArray = decodeBytes(value.encoding, value.value)

internal fun encodeValueBytes(
    value: JsonObject,
): ByteArray {
    val parsed = parseValue(value, emptyMap()).toLongFromBoxed()
    val size = when (value.string("type")) {
        "i64", "f64" -> 8
        else -> 4
    }
    return ByteArray(size) { index -> ((parsed ushr (index * 8)) and 0xff).toByte() }
}

internal fun ByteArray.readLittleEndian(): Long {
    var result = 0L
    forEachIndexed { index, byte ->
        result = result or ((byte.toLong() and 0xff) shl (index * 8))
    }
    return result
}

internal fun ByteArray.toHex(): String = joinToString("") { byte ->
    byte.toUByte().toString(16).padStart(2, '0')
}

private fun decodeBytes(
    encoding: String,
    value: String,
): ByteArray = when (encoding) {
    "utf8" -> value.encodeToByteArray()
    "hex" -> value.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    "base64" -> decodeBase64(value)
    else -> error("Unsupported byte encoding: $encoding")
}

@OptIn(ExperimentalEncodingApi::class)
private fun decodeBase64(
    value: String,
): ByteArray = Base64.decode(value)
