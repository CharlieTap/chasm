package io.github.charlietap.chasm.vm

import kotlin.js.JsName

@JsName("Array")
internal external object ArrayNamespace {
    fun isArray(value: dynamic): Boolean
}

@JsName("Math")
internal external object MathNamespace {
    fun fround(value: dynamic): Double
}

@JsName("Number")
internal external object NumberNamespace {
    fun isInteger(value: dynamic): Boolean
}

internal inline fun newObj(): dynamic = js("({})")

internal inline fun fround(value: dynamic): Double = MathNamespace.fround(value)

internal inline fun isArray(value: dynamic): Boolean = ArrayNamespace.isArray(value)

internal inline fun isInteger(value: dynamic): Boolean = NumberNamespace.isInteger(value)

internal inline fun typeOf(value: dynamic): String = js("typeof value") as String

@JsName("BigInt")
internal external fun BigInt(value: String): dynamic

internal inline fun longToBigInt(value: Long): dynamic = BigInt(value.toString())

internal inline fun bigIntToLong(value: dynamic): Long = value.toString().toLong()

internal val UNDEFINED: dynamic = js("undefined")
