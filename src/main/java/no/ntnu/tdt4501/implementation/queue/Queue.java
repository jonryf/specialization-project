package no.ntnu.tdt4501.implementation.queue;

import no.ntnu.tdt4501.implementation.btree.BTree;

import java.util.List;

public abstract class Queue<K, V> extends BTree<K, V> {

    public abstract K[] getNextElements(int nElements);

    public abstract int size();

    public V deleteAndGetItem(K key) {
        return null;
    }

}
