package io.github.charlietap.chasm.aot.corpus.wasi

import at.released.weh.filesystem.stdio.StdioSink
import at.released.weh.filesystem.stdio.StdioSource
import at.released.weh.host.CommandArgsProvider
import at.released.weh.host.EmbedderHost
import at.released.weh.host.EntropySource
import at.released.weh.host.SystemEnvProvider
import at.released.weh.host.clock.Clock
import at.released.weh.host.clock.MonotonicClock
import io.github.charlietap.chasm.aot.corpus.value.decodeFixtureBytes
import io.github.charlietap.corpus.lib.fixture.FixtureWasiPreview1Host

internal typealias WasiHostFactory = (FixtureWasiPreview1Host, MutableList<Byte>, MutableList<Byte>) -> EmbedderHost

internal fun WasiHostFactory(
    host: FixtureWasiPreview1Host,
    stdout: MutableList<Byte>,
    stderr: MutableList<Byte>,
): EmbedderHost {
    val stdin = host.stdin?.let(::decodeFixtureBytes) ?: ByteArray(0)
    return EmbedderHost {
        this.stdin = StdioSource.Provider { ByteArrayStdioSource(stdin) }
        this.stdout = StdioSink.Provider { CapturingStdioSink(stdout) }
        this.stderr = StdioSink.Provider { CapturingStdioSink(stderr) }
        this.entropySource = ZeroEntropySource
        this.realTimeClock = CountingClock()
        this.monotonicClock = CountingMonotonicClock()
        commandArgs = CommandArgsProvider { host.commandArgs }
        systemEnv = SystemEnvProvider { host.systemEnv }
        fileSystem {
            host.preopenedDirectories.forEach { (hostPath, guestPath) ->
                addPreopenedDirectory(hostPath, guestPath)
            }
        }
    }
}

private object ZeroEntropySource : EntropySource {
    override fun generateEntropy(
        size: Int,
    ): ByteArray = ByteArray(size)
}

private class CountingClock : Clock {
    private var nanos = 0L

    override fun getCurrentTimeEpochNanoseconds(): Long = ++nanos

    override fun getResolutionNanoseconds(): Long = 1L
}

private class CountingMonotonicClock : MonotonicClock {
    private var nanos = 0L

    override fun getTimeMarkNanoseconds(): Long = ++nanos

    override fun getResolutionNanoseconds(): Long = 1L
}
