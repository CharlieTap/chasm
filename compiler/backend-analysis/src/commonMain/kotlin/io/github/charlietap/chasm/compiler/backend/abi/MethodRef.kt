package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
data class MethodRef(
    val owner: String,
    val name: String,
    val parameters: List<String>,
    val returns: String,
) {
    val words: Int = parameters.sumOf(::descriptorWords)

    private val hash: Int = ((owner.hashCode() * 31 + name.hashCode()) * 31 + parameters.hashCode()) * 31 + returns.hashCode()

    override fun hashCode(): Int {
        return hash
    }

    val descriptor: String
        get() = parameters.joinToString("", "(", ")") + returns

    override fun toString(): String = "$owner->$name$descriptor"
}
