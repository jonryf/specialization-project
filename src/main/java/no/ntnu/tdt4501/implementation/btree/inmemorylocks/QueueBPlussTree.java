package no.ntnu.tdt4501.implementation.btree;

import no.ntnu.tdt4501.implementation.btree.inmemorylocks.BPlussTree;
import no.ntnu.tdt4501.implementation.queue.HashTableQueue;
import no.ntnu.tdt4501.implementation.queue.Queue;
import no.ntnu.tdt4501.implementation.queue.SinglyLinkedQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class HBTree<K extends Comparable<? super K>, V> extends BTree<K, V> {
    private final static int THREADS = 1;

    private ExecutorService executorService;
    private Queue<K, V> queue;
    private BTree<K, V> btree;
    private boolean shutdown = false;


    public HBTree() {
        this(new HashTableQueue<>(), new BPlussTree<>());
    }

    public HBTree(Queue<K, V> queue, BTree<K, V> btree) {
        this.executorService = Executors.newFixedThreadPool(THREADS);
        this.queue = queue;
        this.btree = btree;

        new Thread(HBTree.this::moveData).start();
    }

    @Override
    public V search(K key) {
        V result = this.queue.search(key);
        if(result == null) {
            return this.btree.search(key);
        }
        return result;
    }

    @Override
    public void insert(K key, V value) {
        if(this.queue.size() > 5000000){
            //this.btree.insert(key, value);
            this.executorService.submit(() -> {
                this.btree.insert(key, value);
            });
        }else {
            this.queue.insert(key, value);
        }

    }

    @Override
    public void delete(K key) {
        this.queue.delete(key);
        //this.btree.delete(key); //TODO
    }

    public void shutdown(){
        this.shutdown = true;
        this.executorService.shutdown();

    }


    public void moveData3()  {
        int elements = 0;

        while(!this.shutdown) {
            List<Map.Entry<K, V>> data = ((HashTableQueue<K, V>)this.queue).getElements(10000);
            if(data != null){
                int size = data.size();

                int partitions = 4;
                for(int i = 0; i<partitions; i++) {
                    for (int thread = 0; thread < THREADS; thread++) {

                        // CREATE WORKER
                        int finalThread = thread * partitions + i;

                        List<Future> futures = new ArrayList<>();

                        futures.add(this.executorService.submit(() -> {
                            int from = finalThread * size / (THREADS * partitions);
                            int to = (1 + finalThread) * size / (THREADS * partitions);

                            for (Map.Entry<K, V> entry : data.subList(from, to)) {
                                this.btree.insert(entry.getKey(), entry.getValue());
                                this.queue.delete(entry.getKey());
                            }
                        }));
                        for (Future future : futures) {
                            try {
                                future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                
                elements += size;
                int inQueue = this.queue.size();
                System.out.println("Moved elements: " + elements);
                System.out.println("In queue: " + inQueue);
            }
        }
        int inQueue = this.queue.size();
        System.out.println("Shutting down... \n");
        System.out.println("Moved elements: " + elements);
        System.out.println("In queue: " + inQueue);

    }

    // RUNNER;
    public void moveData() {
        int elements = 0;
        List<Future> futures = new ArrayList<>();
        while(!this.shutdown) {
            if (this.queue.size() > 10000) {
                K[] nextElements = this.queue.getNextElements(300);
                if (nextElements != null && nextElements.length > 10000) {
                    Arrays.parallelSort(nextElements);
                    int elm = nextElements.length;
                    elements += elm;


                    for (int slice = 0; slice < THREADS; slice++) {
                        int from = slice * elm / THREADS;
                        int to = (1 + slice) * elm / THREADS;
                        List<V> values = new ArrayList<>();
                        for (int i = from; i < to; i++) {
                            K key = nextElements[i];
                            values.add(this.queue.search(key));
                            this.queue.delete(key);
                        }
                        try {
                            futures.add(this.executorService.submit(() -> {
                                for (int i = from; i < to; i++) {
                                    K key = nextElements[i];
                                    this.btree.insert(key, values.get(i - from));
                                    //TODO this.queue.delete(key);
                                }

                            }));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                for(Future future : futures){
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                  /*  int inQueue = this.queue.size();
                    System.out.println("Moved elements: " + elements);
                    System.out.println("In queue: " + inQueue);*/
                }
            }

        }
        this.executorService.shutdown();
        int inQueue = this.queue.size();
        System.out.println("Shutting down... \n");
        System.out.println("Moved elements: " + elements);
        System.out.println("In queue: " + inQueue);
        long pending = futures.stream().filter(future -> !future.isDone()).count();
        System.out.println(pending + " task are still pending");
    }

    public void moveData2() {
        int elements = 0;
        while(!this.shutdown) {
            List<SinglyLinkedQueue<K, V>.Item<K, V>> nextElements = ((SinglyLinkedQueue<K, V>)this.queue).getNextItems(300);
            if(nextElements != null) {

                List<SinglyLinkedQueue<K, V>.Item<K, V>> sorted =
                        nextElements.parallelStream().sorted(Comparator.comparing(i -> i.key)).collect(Collectors.toList());

                elements += sorted.size();
                for(SinglyLinkedQueue<K, V>.Item<K, V> item : nextElements) {
                    ((SinglyLinkedQueue<K, V>)this.queue).internalRemove(item);
                    this.executorService.submit(() -> {
                        this.btree.insert(item.key, item.value);
                    });
                }
                int inQueue = this.queue.size();
                System.out.println("Moved elements: " + elements);
                System.out.println("In queue: " + inQueue);
            }

        }
        int inQueue = this.queue.size();
        System.out.println("Shutting down... \n");
        System.out.println("Moved elements: " + elements);
        System.out.println("In queue: " + inQueue);
    }

}
