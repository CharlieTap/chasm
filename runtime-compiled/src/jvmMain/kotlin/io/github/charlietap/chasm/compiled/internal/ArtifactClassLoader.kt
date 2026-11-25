package io.github.charlietap.chasm.compiled.internal

import java.io.IOException

internal class ArtifactClassLoader(
    private val className: String,
    private val classes: ClassSource,
    parent: ClassLoader,
) : ClassLoader(parent) {

    private val nestedPrefix = "$className$"

    override fun loadClass(
        name: String,
        resolve: Boolean,
    ): Class<*> {

        if (name != className && !name.startsWith(nestedPrefix)) {
            return super.loadClass(name, resolve)
        }

        synchronized(getClassLoadingLock(name)) {
            val loaded = findLoadedClass(name) ?: findClass(name)
            if (resolve) {
                resolveClass(loaded)
            }
            return loaded
        }
    }

    override fun findClass(name: String): Class<*> {

        val bytes = try {
            classes.classFile(name)
        } catch (failure: IOException) {
            throw ClassNotFoundException(name, failure)
        } ?: throw ClassNotFoundException(name)

        return defineClass(name, bytes, 0, bytes.size)
    }
}
