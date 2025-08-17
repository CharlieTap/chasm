package io.github.charlietap.chasm.gradle

import org.gradle.api.logging.Logger as GradleLogger

interface Logger {
    fun lifecycle(message: String)

    fun error(message: String)
}

class PluginLogger(
    private val logger: GradleLogger,
) : Logger {
    override fun lifecycle(message: String) {
        logger.lifecycle(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }
}
