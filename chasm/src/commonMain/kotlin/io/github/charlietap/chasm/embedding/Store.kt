package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.program.ProgramCompiler
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

fun store(): Store = Store(InternalStore())

/** Creates a store whose modules use the selected optional execution backend. */
fun store(compiler: ProgramCompiler): Store = Store(InternalStore(program = Program(compiler = compiler)))
