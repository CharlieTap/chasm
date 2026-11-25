@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jvm

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.CHUNK_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.compiled.MANAGED_ENTRY_CLASS

// A shrinker that renames the compiled function methods or drops their line numbers leaves traps unattributable, and one
// that merges other classes into the entry into compiled code, or inlines its invoke, blurs where compiled runs begin.
internal fun shrinkerRules(config: CompileConfig): String {

    if (!config.runtime.stacktraces) {
        return ""
    }

    val entry = config.className
    return """
        -keepattributes LineNumberTable
        -keep class $entry${'$'}$CODE_CLASS_PREFIX*, $entry${'$'}$CHUNK_CLASS_PREFIX* { static *** $FUNCTION_METHOD_PREFIX*(...); }
        -keep,allowobfuscation,allowshrinking class $MANAGED_ENTRY_CLASS { *** enter(...); }
        """.trimIndent() + "\n"
}
