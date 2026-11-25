package io.github.charlietap.chasm.compiled.internal

internal class ParentClasses(
    private val parent: ClassLoader,
) {

    fun defines(className: String): Boolean {
        return try {
            Class.forName(className, false, parent)
            true
        } catch (_: ClassNotFoundException) {
            false
        } catch (_: LinkageError) {
            true
        }
    }
}
