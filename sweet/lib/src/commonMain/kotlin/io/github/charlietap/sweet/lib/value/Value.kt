package io.github.charlietap.sweet.lib.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Value {

    @Serializable
    @SerialName("i32")
    data class I32(val value: String? = null) : Value

    @Serializable
    @SerialName("i64")
    data class I64(val value: String? = null) : Value

    @Serializable
    @SerialName("f32")
    data class F32(val value: String? = null) : Value

    @Serializable
    @SerialName("f64")
    data class F64(val value: String? = null) : Value

    @Serializable
    @SerialName("externref")
    data class Extern(val value: String? = null): Value

    @Serializable
    @SerialName("funcref")
    data class Func(val value: String? = null): Value
}
