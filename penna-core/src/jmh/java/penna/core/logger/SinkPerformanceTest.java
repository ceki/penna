package penna.core.logger;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.slf4j.LoggerFactory;
import penna.api.config.Config;
import penna.core.sink.PennaSink;
import penna.core.sink.SinkImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SinkPerformanceTest {

    @State(Scope.Thread)
    public static class SimpleState {
        TreeCache cache = new TreeCache(Config.getDefault());

        PennaLogger logger = cache.getLoggerAt("jmh", "test", "penna");
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void initALogger(Blackhole bh) throws IOException {
        bh.consume(new TreeCache(Config.getDefault()).getLoggerAt("com", "pennacorp", "lePennaApp", "controller", "TheGreatestController"));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void detectLevelIsNotEnabled(SimpleState state) throws IOException {
        state.logger.trace("Should not log");
    }

    @Test
    public void runBenchmarks() throws Exception {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .warmupTime(TimeValue.seconds(15))
                .warmupIterations(3)
                .threads(1)
                .measurementIterations(3)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .addProfiler("gc")
                .build();

        new Runner(options).run();
    }

}
