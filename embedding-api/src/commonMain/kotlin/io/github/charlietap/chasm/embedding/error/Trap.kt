package io.github.charlietap.chasm.embedding.error

import io.github.charlietap.chasm.InternalChasmApi

class Trap
    @InternalChasmApi
    constructor(
        val reason: TrapReason,
        val message: String,
        val memoryAccess: MemoryAccess?,
        val stackTrace: WasmStackTrace?,
    ) {

        override fun toString(): String {
            return buildString {

                when {
                    reason == TrapReason.OTHER -> append(message)
                    message.contains('(') -> append("${reason.description}: $message")
                    else -> append(reason.description)
                }

                memoryAccess?.let { access ->
                    append("\n  ")
                    append(access)
                }

                stackTrace?.let { trace -> append(trace) }
            }
        }
    }

class WasmStackTrace
    @InternalChasmApi
    constructor(
        val frames: List<WasmFrame>,
        val omittedFrames: Int,
        val complete: Boolean,
        private val omissionIndex: Int,
    ) {

        override fun toString(): String {
            return buildString {

                val modules = frames.mapNotNullTo(HashSet()) { frame -> frame.moduleName }
                frames.forEachIndexed { index, frame ->

                    if (index == omissionIndex && omittedFrames > 0) {
                        append("\n  ... ")
                        append(omittedFrames)
                        append(if (omittedFrames == 1) " frame omitted ..." else " frames omitted ...")
                    }

                    append("\n  at ")
                    append(frame.describe(includeModule = modules.size > 1))
                }

                if (omissionIndex >= frames.size && omittedFrames > 0) {
                    append("\n  ... ")
                    append(omittedFrames)
                    append(" frames omitted ...")
                }

                if (!complete) {
                    append("\n  ... trace incomplete")
                }
            }
        }
    }

class UncaughtWasmException(
    val exception: Any?,
    message: String,
) : IllegalStateException(message)
