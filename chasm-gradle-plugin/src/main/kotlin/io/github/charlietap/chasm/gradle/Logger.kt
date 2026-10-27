package io.github.charlietap.chasm.gradle

import org.gradle.api.logging.Logger as GradleLogger

internal class PluginLogger(
    private val logger: GradleLogger,
) : BindgenLogger {
    override fun lifecycle(message: String) {
        logger.lifecycle(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }
}
