package io.github.charlietap.chasm.decoder.decoder.instruction.memory

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index

data class MemArgWithIndex(val memoryIndex: Index.MemoryIndex, val memArg: MemArg)
