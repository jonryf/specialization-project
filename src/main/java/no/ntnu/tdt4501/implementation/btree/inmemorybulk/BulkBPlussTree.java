package no.ntnu.tdt4501.implementation.btree.inmemorybulk;

import no.ntnu.tdt4501.Settings;
import no.ntnu.tdt4501.implementation.btree.BTree;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class BulkBPlussTree<K extends Comparable<? super K>, V> extends BTree<K, V> {
    private ConcurrentHashMap<Integer, BTreeThread> threads = new ConcurrentHashMap<>();
    private int threadIndex = 0;

    public BulkBPlussTree(){
        System.out.println("USING: " + Settings.THREADS);
        for(int t = 0; t < Settings.THREADS; t++){
            BTreeThread thread = new BTreeThread();
            threads.put(t, thread);
        }
    }

    public void shutdown(){
       threads.forEach((key, value) -> value.shutdown = true);
    }

    @Override
    public V search(K key) {
        return getThread(key).search(key);
    }

    @Override
    public void insert(K key, V value) {
        getThread(key).insert(key, value);
    }

    @Override
    public void delete(K key) {
        getThread(key).delete(key);

    }

    public BTreeThread<K, V> getThread(K key){
        if (threadIndex > 10000000){
            threadIndex = 0;
        }
        threadIndex += 1;
        return threads.get(threadIndex % Settings.THREADS);
    }

    class Item<K, V> {
        K key;
        V value;

        public Item(K key, V value){
            this.key = key;
            this.value = value;
        }


    }

    class BTreeThread<K extends Comparable<? super K>, V> extends BTree<K, V> implements Runnable{
        boolean shutdown = false;
        private BPlussTree<K, V> tree;
        private LinkedList<Item<K, V>> items = new LinkedList<>();

        private Thread worker = new Thread(this);


        public BTreeThread(){
            tree = new BPlussTree<>();
            worker.start();
        }

        @Override
        public V search(K key) {
           /* try {
                return null;
                *//*(V) executor.execute(() -> {
                    tree.search(key);
                });*//*
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
            return null;
        }

        @Override
        public void insert(K key, V value) {
            this.items.add(new Item<>(key, value));
            //tree.insert(key, value);
            //executor.submit(() -> tree.insert(key, value));
        }

        @Override
        public void delete(K key) {
            tree.delete(key);
            /*executor.submit(() -> {
                tree.delete(key);
            });*/
        }

        @Override
        public void run() {
            while (!this.shutdown) {
                if (!this.items.isEmpty()) {

                    Item<K, V> item = this.items.poll();
                    if (item != null) {
                        this.tree.insert(item.key, item.value);
                    }
                }
            }
            //System.out.println("Queue size: " + this.items.size());
        }
    }
}
