package io.github.charlietap.chasm.corpus

class MeasurementThread(
    private val thread: FixtureThread,
    private val scheduling: CurrentThreadScheduling,
    private val conditions: ThreadConditions,
    private val pin: Boolean,
) : FixtureThread {

    override fun <T> run(body: () -> T): T {
        return thread.run {

            scheduling.raisePriority()
            val pinnedCpu = if (pin) {
                pinFastestCpu()
            } else {
                null
            }
            conditions.record(scheduling.priority(), scheduling.allowedCpus(), pinnedCpu)

            body()
        }
    }

    private fun pinFastestCpu(): Int {

        val cpu = fastestCpu(cpuIndices(scheduling.allowedCpus())) { index -> scheduling.maxFrequencyKhz(index) }
        scheduling.pin(cpu)

        return cpu
    }
}
