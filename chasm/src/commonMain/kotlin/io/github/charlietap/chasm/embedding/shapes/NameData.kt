package io.github.charlietap.chasm.embedding.shapes

sealed interface NameData

data class FunctionNameData(
    val name: String?,
    val localNames: List<String>?,
) : NameData
