package no.ntnu.tdt4501.benchmark.jmh;

import no.ntnu.tdt4501.Settings;
import no.ntnu.tdt4501.implementation.btree.BTree;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Setup;
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

import static no.ntnu.tdt4501.Settings.THREADS;
import static no.ntnu.tdt4501.Settings.THREADS_TESTS;


public abstract class JMHBenchmark {
    private int index = 0;
    private int searchIndex = 0;
    private int deleteIndex = 0;
    private ExecutorService executorService;

    private BTree<Integer, Integer> instance;

    public abstract BTree<Integer, Integer> getInstance();

    public abstract boolean nativeAsyncSupport();

    public abstract int getThreads();

    private boolean doInitialInsert(){
        return false;
    }

    @Setup
    public void setup() {
        THREADS = getThreads();
        this.instance = getInstance();
        //if(!this.nativeAsyncSupport()) {
            this.executorService = Executors.newWorkStealingPool(THREADS);
        //}
        if (this.doInitialInsert()){
            System.out.println("Start");
            this.insert(5000000);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("End");
        }
    }


    @TearDown
    public void tearDown() {
        //if(!this.nativeAsyncSupport()) {
            this.executorService.shutdown();
        //}
        this.instance.shutdown();

    }

    public void insert(int number){
        if(this.nativeAsyncSupport()){
            this.runInsertSync(number);
        }else {
            this.runInsertAsync(number);
        }
    }

    public void search(int number){
        this.runSearchAsync(number);
    }

    public void delete(int number){
        this.runDeleteAsync(number);
    }



    private void runInsertAsync(int number) {
        List<Future> futures = new ArrayList<>();
        for(int i = 0; i< THREADS*2; i++) {
            futures.add(insertAsync(number/(THREADS*2)));
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

    private void runSearchAsync(int number) {
        List<Future> futures = new ArrayList<>();
        for(int i = 0; i< THREADS*2; i++) {
            futures.add(searchAsync(number/(THREADS*2)));
        }
        for(Future future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void runSearchSync(int count) {
        for(int i = 0; i<count; i++) {
            this.instance.search(searchIndex % index);
            searchIndex += 1;
        }
    }

    private Future searchAsync(int count) {
        return this.executorService.submit(() -> {
            for(int i = 0; i<count; i++) {
                this.instance.search(searchIndex % index);
                searchIndex += 1;
            }
        });
    }



    private Future insertAsync(int count) {
        return this.executorService.submit(() -> {
            for(int i = 0; i<count; i++) {
                this.instance.insert(index, index);
                index += 1;
            }
        });
    }


    private void runDeleteAsync(int number) {
        List<Future> futures = new ArrayList<>();
        for(int i = 0; i< THREADS*2; i++) {
            futures.add(deleteAsync(number/(THREADS*2)));
        }
        for(Future future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void runDeleteSync(int count) {
        for(int i = 0; i<count; i++) {
            this.instance.delete(deleteIndex % index);
            deleteIndex += 1;
        }
    }

    private Future deleteAsync(int count) {
        return this.executorService.submit(() -> {
            int tempDeleteIndex = deleteIndex;
            deleteIndex -= count;
            for(int i = 0; i<count; i++) {
                this.instance.delete(tempDeleteIndex % index);
                tempDeleteIndex += 1;
            }
        });
    }


    public static void run(Class clazz) {
        for (int threads : THREADS_TESTS) {
            System.out.println("----------------------------");
            System.out.println("Testing with " + threads + " threads");
            System.out.println("----------------------------");
            Settings.setThreads(threads);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Options opt = new OptionsBuilder()
                    .include(clazz.getSimpleName())
                    .forks(10)
                    .warmupIterations(0)
                    .measurementTime(TimeValue.seconds(3))
                    .syncIterations(true)
                    .measurementIterations(1)
                    .jvmArgs("-Xms8024m", "-Xmx8024m")
                    .mode(Mode.Throughput)
                    .param("threads", String.valueOf(threads))
                    .build();


            try {
                new Runner(opt).run();
            } catch (RunnerException e) {
                e.printStackTrace();
            }
        }
    }

}
