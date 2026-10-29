package io.github.charlietap.chasm.compiler.backend.abi

data class MethodRef(
    val owner: String,
    val name: String,
    val parameters: List<String>,
    val returns: String,
) {
    val words: Int = parameters.sumOf(::descriptorWords)

    val descriptor: String = parameters.joinToString("", "(", ")") + returns

    private val hash = listOf(owner, name, descriptor).hashCode()

    override fun hashCode(): Int = hash

    override fun toString(): String = "$owner->$name$descriptor"
}
