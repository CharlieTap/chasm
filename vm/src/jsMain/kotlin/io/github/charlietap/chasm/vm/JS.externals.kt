package io.github.charlietap.chasm.vm

import kotlin.js.JsName

@JsName("Array")
external object ArrayNamespace {
    fun isArray(value: dynamic): Boolean
}

@JsName("Math")
external object MathNamespace {
    fun fround(value: dynamic): Double
}

@JsName("Number")
external object NumberNamespace {
    fun isInteger(value: dynamic): Boolean
}

inline fun newObj(): dynamic = js("({})")

inline fun fround(value: dynamic): Double = MathNamespace.fround(value)

inline fun isArray(value: dynamic): Boolean = ArrayNamespace.isArray(value)

inline fun isInteger(value: dynamic): Boolean = NumberNamespace.isInteger(value)

inline fun typeOf(value: dynamic): String = js("typeof value") as String

@JsName("BigInt")
external fun BigInt(value: String): dynamic

inline fun longToBigInt(value: Long): dynamic = BigInt(value.toString())

inline fun bigIntToLong(value: dynamic): Long = value.toString().toLong()

val UNDEFINED: dynamic = js("undefined")
