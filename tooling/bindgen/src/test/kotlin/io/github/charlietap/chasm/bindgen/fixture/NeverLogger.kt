package io.github.charlietap.chasm.bindgen.fixture

import io.github.charlietap.chasm.bindgen.BindgenLogger
import kotlin.test.fail

object NeverLogger : BindgenLogger {
    override fun lifecycle(message: String) = fail("Logger should never be called but was with message: $message")

    override fun error(message: String) = fail("Logger should never be called but was with message: $message")
}
