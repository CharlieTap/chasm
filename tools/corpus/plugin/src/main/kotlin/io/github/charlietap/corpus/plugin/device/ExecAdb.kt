package io.github.charlietap.corpus.plugin.device

import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ExecAdb(
    private val execOperations: ExecOperations,
    private val adbPath: String,
) : Adb {

    override fun run(vararg arguments: String): AdbOutput {

        val output = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine(adbPath, *arguments)
            standardOutput = output
            errorOutput = output
            isIgnoreExitValue = true
        }

        return AdbOutput(result.exitValue, output.toString())
    }

    override fun stream(
        arguments: List<String>,
        line: (String) -> Unit,
    ): Int {

        val lines = SynchronizedLines(line)
        val standardLines = LineOutputStream(lines)
        val errorLines = LineOutputStream(lines)

        val result = try {
            execOperations.exec {
                commandLine(listOf(adbPath) + arguments)
                standardOutput = standardLines
                errorOutput = errorLines
                isIgnoreExitValue = true
            }
        } finally {
            standardLines.close()
            errorLines.close()
        }

        val failure = lines.failure()
        if (failure != null) {
            throw failure
        }

        return result.exitValue
    }

    private class SynchronizedLines(
        private val line: (String) -> Unit,
    ) {

        private var callbackFailure: Exception? = null

        @Synchronized
        fun accept(text: String) {
            if (callbackFailure == null) {
                try {
                    line(text)
                } catch (exception: Exception) {
                    callbackFailure = exception
                }
            }
        }

        @Synchronized
        fun failure(): Exception? {
            return callbackFailure
        }
    }

    private class LineOutputStream(
        private val lines: SynchronizedLines,
    ) : OutputStream() {

        private val buffer = ByteArrayOutputStream()

        override fun write(byte: Int) {

            if (byte == NEWLINE) {
                flushLine()
            } else {
                buffer.write(byte)
            }
        }

        override fun close() {

            if (buffer.size() > 0) {
                flushLine()
            }
        }

        private fun flushLine() {
            lines.accept(buffer.toString(Charsets.UTF_8))
            buffer.reset()
        }

        private companion object {
            const val NEWLINE = '\n'.code
        }
    }
}
