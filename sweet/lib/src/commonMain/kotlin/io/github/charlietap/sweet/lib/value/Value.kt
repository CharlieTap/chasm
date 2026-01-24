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
    @SerialName("anyref")
    data class AnyRef(val value: String? = null): Value

    @Serializable
    @SerialName("arrayref")
    data class ArrayRef(val value: String? = null): Value

    @Serializable
    @SerialName("refnull")
    data class BottomRef(val value: String? = null): Value

    @Serializable
    @SerialName("eqref")
    data class EqRef(val value: String? = null): Value

    @Serializable
    @SerialName("exnref")
    data class ExceptionRef(val value: String? = null): Value

    @Serializable
    @SerialName("externref")
    data class ExternRef(val value: String? = null): Value

    @Serializable
    @SerialName("funcref")
    data class FuncRef(val value: String? = null): Value

    @Serializable
    @SerialName("i31ref")
    data class I31Ref(val value: String? = null): Value

    @Serializable
    @SerialName("nullref")
    data class NullRef(val value: String? = null): Value

    @Serializable
    @SerialName("nullfuncref")
    data class NullFuncRef(val value: String? = null): Value

    @Serializable
    @SerialName("nullexnref")
    data class NullExceptionRef(val value: String? = null): Value

    @Serializable
    @SerialName("nullexternref")
    data class NullExternRef(val value: String? = null): Value

    @Serializable
    @SerialName("structref")
    data class StructRef(val value: String? = null): Value

    @Serializable
    @SerialName("v128")
    data class V128(
        @SerialName("lane_type")
        val laneType: String,
        val value: List<String>
    ): Value

    @Serializable
    @SerialName("either")
    data class Either(val values: List<Value>): Value
}
