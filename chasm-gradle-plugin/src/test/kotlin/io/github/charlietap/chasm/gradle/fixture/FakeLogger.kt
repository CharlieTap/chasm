package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.Logger
import kotlin.test.assertEquals

class FakeLogger : Logger {

    private sealed interface Message {
        val message: String

        @JvmInline
        value class Lifecycle(override val message: String) : Message

        @JvmInline
        value class Error(override val message: String) : Message
    }

    private val output = mutableListOf<Message>()

    override fun lifecycle(message: String) {
        output += Message.Lifecycle(message)
    }

    override fun error(message: String) {
        output += Message.Error(message)
    }

    fun expectExactlyOneLifecycleLog(message: String) {
        if (output.size != 1) throw AssertionError("Logger does not contain exactly one entry, $output")
        val log = (output.first() as? Message.Lifecycle)?.message
        assertEquals(message, log)
    }

    fun expectExactlyOneErrorLog(message: String) {
        if (output.size != 1) throw AssertionError("Logger does not contain exactly one entry, $output")
        val log = (output.first() as? Message.Error)?.message
        assertEquals(message, log)
    }
}
