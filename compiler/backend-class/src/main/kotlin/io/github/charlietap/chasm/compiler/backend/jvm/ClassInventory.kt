package io.github.charlietap.chasm.compiler.backend.jvm

import java.io.OutputStream

internal class ClassInventory(
    private val output: JvmOutput,
) : JvmOutput {

    private val written = ArrayList<String>()

    val classNames: List<String>
        get() = written

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {

        output.classFile(internalName, bytes)

        written += internalName.replace('/', '.')
    }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T {
        return output.resource(path, write)
    }
}
