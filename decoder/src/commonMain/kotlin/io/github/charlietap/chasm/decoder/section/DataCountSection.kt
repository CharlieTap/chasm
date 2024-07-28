package io.github.charlietap.chasm.decoder.section

import kotlin.jvm.JvmInline

@JvmInline
internal value class DataCountSection(val dataCount: UInt) : Section
