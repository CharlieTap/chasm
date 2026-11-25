package io.github.charlietap.chasm.compiled.internal

internal class ClassOwnership {

    fun foreignClass(
        classes: List<String>,
        loader: ClassLoader,
    ): String? {
        return classes.firstOrNull { name -> !defined(name, loader) }
    }

    private fun defined(
        name: String,
        loader: ClassLoader,
    ): Boolean {
        return try {
            Class.forName(name, false, loader).classLoader === loader
        } catch (_: ClassNotFoundException) {
            false
        } catch (_: LinkageError) {
            false
        }
    }
}
