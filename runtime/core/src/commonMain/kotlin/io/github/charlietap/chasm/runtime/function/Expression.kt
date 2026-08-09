package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.program.EXIT_IP
import kotlin.jvm.JvmInline

@JvmInline
value class Expression(val entryIp: Int) {
    companion object {
        val EMPTY = Expression(EXIT_IP)
    }
}
