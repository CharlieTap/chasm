package io.github.charlietap.corpus.plugin.device

interface Adb {

    fun run(vararg arguments: String): AdbOutput

    fun stream(
        arguments: List<String>,
        line: (String) -> Unit,
    ): Int
}

data class AdbOutput(
    val exitCode: Int,
    val text: String,
)
