package no.ntnu.tdt4501.benchmark.jmh;

import no.ntnu.tdt4501.implementation.btree.BTree;
import no.ntnu.tdt4501.implementation.btree.inmemory.InMemoryBPlussTree;
import no.ntnu.tdt4501.implementation.btree.inmemorybulk.BulkBPlussTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
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

import static no.ntnu.tdt4501.Settings.THREADS;


public abstract class JMHBenchmark2 {
    private int index = 0;
    private ExecutorService executorService;

    private BTree<Integer, Integer> instance;

    public abstract BTree<Integer, Integer> getInstance();

    public abstract boolean nativeAsyncSupport();


    @Setup
    public void setup() {
        this.instance = getInstance();
        if(!this.nativeAsyncSupport()) {
            this.executorService = Executors.newFixedThreadPool(THREADS);
        }
    }


    @TearDown
    public void tearDown() {
        this.executorService.shutdown();
        this.instance.shutdown();

    }






    public void insert(int number){
        if(this.nativeAsyncSupport()){
            this.runInsertSync(number);
        }else {
            this.runInsertAsync(number);
        }
    }

    private void runInsertAsync(int number) {
        List<Future> futures = new ArrayList<>();
        for(int i = 0; i< THREADS; i++) {
            futures.add(insertAsync(number/THREADS));
        }
        for(Future future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void runInsertSync(int count) {
        for(int i = 0; i<count; i++) {
            this.instance.insert(index, index);
            index += 1;
        }
    }

    private Future insertAsync(int count) {
        return this.executorService.submit(() -> {
            for(int i = 0; i<count; i++) {
                this.instance.insert(("saav" + index).hashCode(), index);
                index += 1;
            }
        });
    }



}
