package io.github.charlietap.corpus.plugin.device

class FakeAdb(
    private val responses: Map<String, AdbOutput>,
) : Adb {

    val commands = mutableListOf<String>()

    override fun run(vararg arguments: String): AdbOutput {

        val command = arguments.joinToString(" ")
        commands += command

        return responses[command] ?: throw AssertionError("unexpected adb command: $command")
    }

    override fun stream(
        arguments: List<String>,
        line: (String) -> Unit,
    ): Int {
        throw AssertionError("unexpected adb stream: ${arguments.joinToString(" ")}")
    }
}
