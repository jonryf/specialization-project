package no.ntnu.tdt4501.implementation.btree.inmemorylocks;

import no.ntnu.tdt4501.implementation.btree.BTree;
import no.ntnu.tdt4501.implementation.queue.HashTableQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static no.ntnu.tdt4501.Settings.THREADS;

@SuppressWarnings("Duplicates")
public class QueueBPlussTree<K extends Comparable<? super K>, V> extends BTree<K, V> {

    //private ExecutorService executorService;
    public  ExecutorService executorService;

    private HashTableQueue<K, V> queue;
    private BTree<K, V> btree;
    private boolean shutdown = false;

    private List<Map.Entry<K, V>> tempQueue;


    public QueueBPlussTree() {
        this(new HashTableQueue<>(), new BPlussTree<>());
    }

    public QueueBPlussTree(HashTableQueue<K, V> queue, BTree<K, V> btree) {
        //this.executorService = Executors.newFixedThreadPool(THREADS);
        this.queue = queue;
        this.btree = btree;
        executorService = Executors.newWorkStealingPool(THREADS);
        executorService.execute(this::moveDataAndSort);
        //new Thread(QueueBPlussTree.this::moveData3).start();
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
        if(this.queue.size() > 1000000){
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
        V value = this.queue.deleteAndGetItem(key);
        if (value == null){
            this.btree.delete(key);
        }
    }

    @Override
    public void shutdown(){
        this.shutdown = true;
        this.executorService.shutdown();

    }

    private List<Map.Entry<K, V>> collectData(){
        return (this.queue).getElements(10000);
    }


    public void moveDataAndSort()  {
        int elements = 0;
        Future<List<Map.Entry<K, V>>> nextDataBatch = this.executorService.submit(this::collectData);
        while(!this.shutdown) {
            List<Map.Entry<K, V>> data = null;
            try {
                data = nextDataBatch.get();
                if(data == null){
                    nextDataBatch = this.executorService.submit(this::collectData);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(data != null){
                int size = data.size();
                int partitions = 20;


                List<Future> futures = new ArrayList<>();
                nextDataBatch = this.executorService.submit(this::collectData);
                for(int i = 0; i<partitions; i++) {
                    for (int thread = 0; thread < THREADS; thread++) {

                        // CREATE WORKER
                        int task = thread * partitions + i;
                        List<Map.Entry<K, V>> finalData = data;
                        futures.add(this.executorService.submit(() -> {
                            int from = task * size / (THREADS * partitions);
                            int to = (1 + task) * size / (THREADS * partitions);

                            for (Map.Entry<K, V> entry : finalData.subList(from, to)) {
                                this.btree.insert(entry.getKey(), entry.getValue());
                                //this.queue.delete(entry.getKey());
                            }
                        }));
                    }
                }
                for (Future future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                
                elements += size;
                int inQueue = this.queue.size();
                System.out.println("Moved elements: " + elements);
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

    /*public void moveData2() {
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
    }*/

}
