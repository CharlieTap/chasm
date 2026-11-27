package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.CODE_CLASS_PREFIX

@InternalChasmApi
class GeneratedClassNames(
    private val entryPath: String,
) {

    fun module(): String {
        return "L$entryPath;"
    }

    fun code(batch: Int): String {
        return "L$entryPath\$$CODE_CLASS_PREFIX$batch;"
    }

    fun signature(index: Int): String {
        return "L$entryPath\$Sig$index;"
    }

    fun declare(index: Int): String {
        return "L$entryPath\$Declare$index;"
    }

    fun structs(group: Int): String {
        return "L$entryPath\$Structs$group;"
    }

    fun constants(): String {
        return "L$entryPath\$Constants;"
    }
}
