@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.sse2.ext

import libsse2.sse2_sqrt

actual inline fun Double.fsqrt() = sse2_sqrt(this)
