package no.ntnu.tdt4501.implementation.queue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HashTableQueue<K extends Comparable<? super K>, V> extends Queue<K, V> {

    public ConcurrentHashMap<K,V> hashtable;


    public HashTableQueue() {
        hashtable = new ConcurrentHashMap<>(1010000, 4f,4);
    }

    @Override
    public V search(K key) {
        return this.hashtable.get(key);
    }

    @Override
    public void insert(K key, V value) {
        this.hashtable.put(key, value);
    }

    @Override
    public void delete(K key) {
        this.hashtable.remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public K[] getNextElements(int nElements) {
        List<K> keys = hashtable.keySet().parallelStream().collect(Collectors.toList());
        if(keys.size() == 0){
            return null;
        }
        K[] arr = (K[]) Array.newInstance(keys.get(0).getClass(), keys.size());
        return keys.toArray(arr);
        /*
        for(int i = 0; i<nElements && hashtable.keys().hasMoreElements(); i++){
            K key = hashtable.keys().nextElement();
            keys.add(key);
        }
        if(keys.size() == 0){
            return null;
        }

        K[] arr = (K[]) Array.newInstance(keys.get(0).getClass(), keys.size());
        return keys.toArray(arr);*/
    }

    public List<Map.Entry<K, V>> getElements(int minimum) {
        if(this.size() < minimum){
            return null;
        }
        List<Map.Entry<K, V>> data =
                this.hashtable.entrySet().parallelStream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
        return data;


    }

    @Override
    public int size() {
        return this.hashtable.size();
    }
}
