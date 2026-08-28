package io.github.charlietap.chasm.fixture.runtime.function

import io.github.charlietap.chasm.runtime.program.EXIT_IP
import kotlin.jvm.JvmInline

@JvmInline
value class RuntimeExpression(val entryIp: Int)

fun runtimeExpression(entryIp: Int = EXIT_IP) = RuntimeExpression(entryIp)
