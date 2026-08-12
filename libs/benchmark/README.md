# Benchmark placement

This library helps JVM benchmarks run on a consistent class of Apple silicon
CPU core.

Modern Apple silicon has three kinds of CPU core across the product family:

- **Efficiency cores** use less power but do less work per clock.
- **Performance cores** are faster.
- **Super cores** are the fastest cores in the M5 family.

An individual chip may contain only two of these classes. For example, the
18-core M5 Pro used to develop this library has 12 Performance cores and 6
Super cores.

macOS moves a thread between cores while it runs. A benchmark can therefore
start on a Super core and later run on a slower core. That movement adds noise:
the same code can report different times even though the code did not change.

macOS does not offer normal applications a public way to pin a thread to one
exact CPU. This library does the next best thing:

1. Ask macOS to prefer the fastest core class.
2. Wait until the benchmark thread reaches that class.
3. Check the thread's core before and after the measured work.
4. Reject a result when those checks are not consistent.

This does not prove that the thread stayed on the fastest class at every instant,
but it catches bad placement without adding sampling overhead inside the timed
region.

## JVM usage

```kotlin
val topology = BenchmarkStabilizer.topology()
val policy = BenchmarkStabilizer.enter(BenchmarkMode.PREFER_FASTEST, topology)
check(policy.canProceed) { policy.message.orEmpty() }

val start = BenchmarkStabilizer.awaitFastestCore(topology = topology)
check(!topology.isPlacementSupported || start.isFastest == true)

// Run the timed work here.

check(BenchmarkStabilizer.finishTrial(start, topology).isValid)
```

`PREFER_FASTEST` is the normal mode for JVM benchmarks. `SHORT_REALTIME` is only
for very short experiments and requires untimed checkpoints to avoid macOS's
realtime failsafe. The implementation is JVM-only; other Kotlin targets return
an explicit unsupported result. On Linux, Windows, and Macs whose core classes
cannot be detected, the high-level API is a no-op so the benchmark still runs.
