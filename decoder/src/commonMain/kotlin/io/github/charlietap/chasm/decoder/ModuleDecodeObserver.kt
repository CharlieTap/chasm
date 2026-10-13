package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module

/** Follows a module while [ParallelWasmModuleDecoder] decodes it. Implementations must not throw. */
interface ModuleDecodeObserver {

    /**
     * Called once, before any body is observed, with the module as it stands ahead of the
     * code section: functions have their types but no bodies, and there are no data segments.
     * [dataCount] is the value of the data count section, when there is one.
     *
     * Returns whether to go on following the module.
     */
    fun prelude(module: Module, dataCount: UInt?): Boolean

    /** Called by each thread that decodes bodies, for an observer of its own. */
    fun bodyObserver(): FunctionBodyObserver

    /**
     * Called once the sections after the code section have decoded, which may be while
     * bodies are still being observed on other threads.
     */
    fun remainder(dataSegments: List<DataSegment>)
}

/**
 * Follows function bodies on one thread: [begin] once a body's locals are known, [instruction]
 * for each instruction with the opcode it was decoded from or [UNKNOWN_OPCODE], and [end] when
 * the body has decoded completely. A body that fails to decode is never ended.
 */
interface FunctionBodyObserver {

    fun begin(bodyIndex: Int, locals: List<Local>)

    fun instruction(opcode: Int, instruction: Instruction)

    fun end()

    companion object {

        /** Stands in for an opcode that is not known. No instruction is encoded with it. */
        const val UNKNOWN_OPCODE: Int = 0xFF
    }
}
