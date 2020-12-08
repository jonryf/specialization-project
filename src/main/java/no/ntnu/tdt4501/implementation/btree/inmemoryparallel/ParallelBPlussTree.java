package no.ntnu.tdt4501.implementation.btree.inmemoryparallel;

import no.ntnu.tdt4501.Settings;
import no.ntnu.tdt4501.implementation.btree.BTree;

import java.util.concurrent.ConcurrentHashMap;


public class ParallelBPlussTree<K extends Comparable<? super K>, V> extends BTree<K, V> {
    private ConcurrentHashMap<Integer, BTreeThread<K, V>> threads = new ConcurrentHashMap<>();
    private int threadIndex = 0;

    public ParallelBPlussTree(){
        System.out.println("USING: " + Settings.THREADS);
        for(int t = 0; t < Settings.THREADS; t++){
            BTreeThread<K, V> thread = new BTreeThread<>();
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

    private BTreeThread<K, V> getThread(K key){
        return threads.get(Math.abs(key.hashCode() % Settings.THREADS));
    }

    class Item<K, V> {
        K key;
        V value;

        public Item(K key, V value){
            this.key = key;
            this.value = value;
        }


    }

}
