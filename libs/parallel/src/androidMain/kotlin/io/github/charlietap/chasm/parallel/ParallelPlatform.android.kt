package io.github.charlietap.chasm.parallel

actual fun availableParallelProcessors(): Int = Runtime.getRuntime().availableProcessors()
