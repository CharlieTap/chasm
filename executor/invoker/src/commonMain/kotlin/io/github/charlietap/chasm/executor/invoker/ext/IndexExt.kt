@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.instruction.Index

inline fun Index.index() = idx.toInt()
