package no.ntnu.tdt4501.benchmark;

import no.ntnu.tdt4501.implementation.btree.BTree;
import no.ntnu.tdt4501.implementation.btree.HBTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@State(Scope.Thread)
public class JMHBenchmark {
    private final static int THREADS = 1;

    private BTree<Integer, Integer> instance;
    private int index = 0;
    private ExecutorService executorService;

    @Setup
    public void setup() {
        System.out.println("reset ");
        this.instance = new HBTree<>();
        System.out.println("Testing: " + instance.getClass().getName());
        this.executorService = Executors.newFixedThreadPool(THREADS);

    }

    @TearDown
    public void tearDown() {
        if(this.instance instanceof HBTree) {
            ((HBTree) this.instance).shutdown();
        }
        this.executorService.shutdown();
        System.out.println("After test;");
        for (int i = 5000; i<5020; i++){
            System.out.println(this.instance.search(("saav" + i).hashCode()));
        }

    }


   /* @Benchmark
    @OperationsPerInvocation(1000)
    public void benchmarkInsert() {
        runInsert(1000);
    }*/

    @Benchmark
    @OperationsPerInvocation(10000)
    public void benchmarkInsertAsync() {
        List<Future> futures = new ArrayList<>();
        for(int i = 0; i< THREADS; i++) {
            futures.add(runInsertAsync(10000/THREADS));
        }
        for(Future future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void runInsert(int count) {
        for(int i = 0; i<count; i++) {
            this.instance.insert(("saav" + index).hashCode(), index);

            index += 1;
        }
    }

    public Future runInsertAsync(int count) {
        return this.executorService.submit(() -> {
            for(int i = 0; i<count; i++) {
                this.instance.insert(("saav" + index).hashCode(), index);
                index += 1;
            }
        });
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(0)
                .measurementTime(TimeValue.seconds(10))
                //.shouldDoGC(false)
                //.jvmArgs("-XX:ActiveProcessorCount=8")
                .syncIterations(true)
                .measurementIterations(2)
                .mode(Mode.Throughput)
                .build();


        new Runner(opt).run();
    }
}
