package io.github.charlietap.chasm.gradle.fixture

import io.github.charlietap.chasm.gradle.Logger
import kotlin.test.fail

object NeverLogger : Logger {
    override fun lifecycle(message: String) = fail("Logger should never be called but was with message: $message")

    override fun error(message: String) = fail("Logger should never be called but was with message: $message")
}
