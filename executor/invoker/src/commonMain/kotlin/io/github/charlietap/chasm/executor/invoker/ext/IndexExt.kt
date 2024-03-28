@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.ast.module.Index

inline fun Index.index() = idx.toInt()
