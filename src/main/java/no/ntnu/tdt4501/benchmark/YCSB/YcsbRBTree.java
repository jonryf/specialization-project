package no.ntnu.tdt4501.benchmark.YCSB;

import no.ntnu.tdt4501.implementation.btree.BTree;
import no.ntnu.tdt4501.implementation.btree.inmemorylocks.QueueBPlussTree;
import no.ntnu.tdt4501.implementation.btree.inmemorylocks.BPlussTree;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class YcsbRBTree extends DB {
    private BTree<String, Long> instance;
    private final static int THREADS = 1;
    private ExecutorService executorService;
    private int inProgress = 0;
    private int outProgress = 0;
    private List<Future> inQueue = new ArrayList<>();

    /**
     * Initialize any state for this DB. Called once per DB instance; there is one
     * DB instance per client thread.
     */
    @Override
    public void init(){
        this.instance = new BPlussTree<>();
        this.executorService = Executors.newFixedThreadPool(THREADS);

    }

    @Override
    public void cleanup(){
        if(this.instance instanceof QueueBPlussTree) {
            ((QueueBPlussTree) this.instance).shutdown();
        }
        this.executorService.shutdown();
        long pending = inQueue.stream().filter(future -> !future.isDone()).count();

        System.out.println(pending + " task are still pending");
    }

    @Override
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
        return Status.NOT_IMPLEMENTED;
    }

    @Override
    public Status scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
        return Status.NOT_IMPLEMENTED;
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {
        return Status.NOT_IMPLEMENTED;
    }

    @Override
    public Status insert(String table, String key, Map<String, ByteIterator> values) {
        /*if(inProgress-outProgress > 50000){
            try {
                System.out.println(inProgress-outProgress);
                long pending = inQueue.stream().filter(future -> !future.isDone()).count();
                inProgress = (int) pending;
                outProgress = 0;
                System.out.println(pending + " task are still pending");
                if (pending > 50000) {
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.inQueue.add(this.executorService.submit(() -> {
            this.inProgress += 1;
            this.instance.insert(key, 10L);
            this.outProgress += 1;
        }));*/
        this.instance.insert(key, 10L);
        return Status.OK;
    }

    @Override
    public Status delete(String table, String key) {
        return Status.NOT_IMPLEMENTED;
    }
}
