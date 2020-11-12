package no.ntnu.tdt4501.benchmark.jmh.inmemory;

import no.ntnu.tdt4501.benchmark.jmh.JMHBenchmark;
import no.ntnu.tdt4501.implementation.btree.BTree;
import no.ntnu.tdt4501.implementation.btree.inmemory.InMemoryBPlussTree;
import no.ntnu.tdt4501.implementation.btree.inmemorybulk.BulkBPlussTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

@State(Scope.Thread)
public class BulkBenchmark extends JMHBenchmark {
    @Param({"1", "2", "4", "8", "16"})
    private int threads;


    public BTree<Integer, Integer> getInstance() {
        return new BulkBPlussTree<>();
    }

    public int getThreads(){
        return this.threads;
    }

    @Override
    public boolean nativeAsyncSupport() {
        return true;
    }

    @Threads(1)
    @Benchmark
    @OperationsPerInvocation(1000)
    public void insertTest() {
        insert(1000);
    }


    public static void main(String[] args) {
        JMHBenchmark.run(BulkBenchmark.class);
    }



}
