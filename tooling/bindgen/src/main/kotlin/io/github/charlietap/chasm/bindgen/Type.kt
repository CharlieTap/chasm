package io.github.charlietap.chasm.bindgen

import java.io.Serializable

interface Type : Serializable

enum class Scalar : Type {
    Integer,
    Long,
    Float,
    Double,
    String,
    Unit,
}
