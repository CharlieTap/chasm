package io.github.charlietap.chasm.corpus

import kotlin.test.Test
import kotlin.test.assertEquals

class MeasurementThreadTest {

    @Test
    fun `raises the priority on the fixture thread and records what it reads back before the body runs`() {

        val events = mutableListOf<String>()
        val thread = MeasurementThread(
            thread = RecordingFixtureThread(events),
            scheduling = FakeThreadScheduling(events),
            conditions = RecordingThreadConditions(events),
            pin = false,
        )

        val result = thread.run {
            events += "body"
            42
        }

        assertEquals(42, result)
        assertEquals(
            listOf(
                "thread started",
                "priority raised",
                "priority read",
                "cpus read",
                "recorded priority=-4 cpus=0-7 pinnedCpu=null",
                "body",
                "thread finished",
            ),
            events,
        )
    }

    @Test
    fun `pins the fixture thread to the fastest allowed cpu and records the cpus read back after pinning`() {

        val events = mutableListOf<String>()
        val thread = MeasurementThread(
            thread = RecordingFixtureThread(events),
            scheduling = FakeThreadScheduling(events),
            conditions = RecordingThreadConditions(events),
            pin = true,
        )

        thread.run {
            events += "body"
        }

        assertEquals(
            listOf(
                "thread started",
                "priority raised",
                "cpus read",
                "frequency of 0 read",
                "frequency of 1 read",
                "frequency of 2 read",
                "frequency of 3 read",
                "frequency of 4 read",
                "frequency of 5 read",
                "frequency of 6 read",
                "frequency of 7 read",
                "pinned to 6",
                "priority read",
                "cpus read",
                "recorded priority=-4 cpus=6 pinnedCpu=6",
                "body",
                "thread finished",
            ),
            events,
        )
    }

    private class RecordingFixtureThread(
        private val events: MutableList<String>,
    ) : FixtureThread {

        override fun <T> run(body: () -> T): T {

            events += "thread started"
            val result = body()
            events += "thread finished"

            return result
        }
    }

    private class FakeThreadScheduling(
        private val events: MutableList<String>,
    ) : CurrentThreadScheduling {

        private var allowedCpus = "0-7"

        override fun raisePriority() {
            events += "priority raised"
        }

        override fun priority(): Int {

            events += "priority read"

            return -4
        }

        override fun allowedCpus(): String {

            events += "cpus read"

            return allowedCpus
        }

        override fun maxFrequencyKhz(cpu: Int): Long {

            events += "frequency of $cpu read"

            return if (cpu < 6) 1_800_000 else 2_900_000
        }

        override fun pin(cpu: Int) {

            events += "pinned to $cpu"
            allowedCpus = cpu.toString()
        }
    }

    private class RecordingThreadConditions(
        private val events: MutableList<String>,
    ) : ThreadConditions {

        override fun record(
            priority: Int,
            cpus: String,
            pinnedCpu: Int?,
        ) {
            events += "recorded priority=$priority cpus=$cpus pinnedCpu=$pinnedCpu"
        }
    }
}
