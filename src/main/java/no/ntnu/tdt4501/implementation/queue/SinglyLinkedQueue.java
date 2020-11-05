package no.ntnu.tdt4501.implementation.queue;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class SinglyLinkedQueue<K extends Comparable<? super K>, V> extends Queue<K, V> {

    public class Item<K, V> {
        public Item(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K key;
        public V value;
    }

    public ConcurrentLinkedQueue<Item<K, V>> queue;

    public SinglyLinkedQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public V search(K key) {
        Item<K, V> item = this.findItem(key);
        if(item != null){
            return item.value;
        }
        return null;
    }

    @Override
    public void insert(K key, V value) {
        this.queue.add(new Item<>(key, value));
    }

    @Override
    public void delete(K key) {
        Item<K, V> item = this.findItem(key);
        if(item != null) {
            this.queue.remove(item);
        }
    }

    private Item<K, V> findItem(K key){
        return this.queue.parallelStream().filter(i -> i.key == key).findFirst().orElse(null);
    }

    public List<Item<K, V>> getNextItems(int nElements) {
        return this.queue.parallelStream().collect(Collectors.toList());
    }

    public void internalRemove(Item<K, V> item){
        this.queue.remove(item);
    }



        @SuppressWarnings("unchecked")
    @Override
    public K[] getNextElements(int nElements) {
        List<K> keys = this.queue.parallelStream().map(i -> i.key).collect(Collectors.toList());
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

    @Override
    public int size() {
        return this.queue.size();
    }
}
