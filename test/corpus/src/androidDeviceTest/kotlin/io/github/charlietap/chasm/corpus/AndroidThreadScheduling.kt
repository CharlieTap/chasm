package io.github.charlietap.chasm.corpus

import android.os.Process
import java.io.File
import java.math.BigInteger

class AndroidThreadScheduling : CurrentThreadScheduling {

    override fun raisePriority() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)
    }

    override fun priority(): Int {
        return Process.getThreadPriority(Process.myTid())
    }

    override fun allowedCpus(): String {
        return File("/proc/self/task/${Process.myTid()}/status").useLines { lines ->
            lines.first { line -> line.startsWith(ALLOWED_CPUS_FIELD) }.substringAfter(':').trim()
        }
    }

    override fun maxFrequencyKhz(cpu: Int): Long {
        return File("/sys/devices/system/cpu/cpu$cpu/cpufreq/cpuinfo_max_freq").readText().trim().toLong()
    }

    override fun pin(cpu: Int) {

        val tid = Process.myTid()
        val mask = BigInteger.ONE.shiftLeft(cpu).toString(16)
        val command = listOf(TASKSET, "-p", mask, tid.toString())

        val taskset = ProcessBuilder(command).redirectErrorStream(true).start()
        val output = taskset.inputStream.bufferedReader().use { reader -> reader.readText() }.trim()
        val exitCode = taskset.waitFor()

        check(exitCode == 0) {
            "Pinning the fixture thread to CPU $cpu failed: '${command.joinToString(" ")}' exited with $exitCode: $output"
        }
    }

    private companion object {
        const val ALLOWED_CPUS_FIELD = "Cpus_allowed_list"
        const val TASKSET = "/system/bin/taskset"
    }
}
