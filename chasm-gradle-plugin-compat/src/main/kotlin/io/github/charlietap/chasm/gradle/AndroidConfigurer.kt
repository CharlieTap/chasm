package io.github.charlietap.chasm.gradle

interface AndroidConfigurer {
    fun configure(androidComponents: Any, context: AndroidConfigContext)
}
