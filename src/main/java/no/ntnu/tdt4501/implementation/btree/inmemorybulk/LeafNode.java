package no.ntnu.tdt4501.implementation.btree.inmemorybulk.inmemorylocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("Duplicates")
public class LeafNode<K extends Comparable<? super K>, V> extends Node<K, V> {
    private volatile List<K> keys;
    private volatile List<V> values;
    private final BPlussTree<K,V> tree;
    private volatile LeafNode next;


    LeafNode(BPlussTree<K, V> tree) {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.tree = tree;
    }

    @Override
    public V getValue(K key) {
        int loc = Collections.binarySearch(this.keys, key);
        if(loc >= 0) {
            return this.values.get(loc);
        }else {
            return null;
        }
    }

    @Override
    public synchronized void deleteValue(K key, int branchingFactor) {

        int loc = Collections.binarySearch(keys, key);
        if (loc >= 0) {
            keys.remove(loc);
            values.remove(loc);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void insertValue(K key, V value, int branchingFactor) {
        synchronized (this) {
            int loc = Collections.binarySearch(keys, key);
            int valueIndex = loc >= 0 ? loc : -loc - 1;
            if (loc >= 0) { //TODO
                values.set(valueIndex, value);
            } else {
                keys.add(valueIndex, key);
                values.add(valueIndex, value);
            }
        }
        if (this.tree.root.isOverflow(branchingFactor)) {
            Node<K, V> sibling = split();
            InternalNode<K, V> newRoot = new InternalNode<>(this.tree);
            newRoot.keys.add(sibling.getFirstLeafKey());
            newRoot.children.add(this);
            newRoot.children.add(sibling);
            this.tree.updateRootNode(newRoot);
        }
    }

    @Override
    public K getFirstLeafKey() {
        return this.keys.get(0);
    }

    @Override
    public synchronized void merge(Node sibling) {
        @SuppressWarnings("unchecked")
        LeafNode<K, V> node = (LeafNode) sibling;
        keys.addAll(node.keys);
        values.addAll(node.values);
        next = node.next;
    }


    @Override
    public synchronized Node<K, V> split() {
        LeafNode<K, V> sibling = new LeafNode<>(this.tree);
        int from = (keyNumber() + 1) / 2, to = keyNumber();
        sibling.keys.addAll(keys.subList(from, to));
        sibling.values.addAll(values.subList(from, to));

        keys.subList(from, to).clear();
        values.subList(from, to).clear();

        sibling.next = next;
        next = sibling;
        return sibling;
    }

    @Override
    public boolean isOverflow(int branchingFactor) {
        return this.values.size() > branchingFactor - 1;
    }

    @Override
    public boolean isUnderflow(int branchingFactor) {
        return this.values.size() < branchingFactor / 2;
    }

    @Override
    public int keyNumber() {
        return this.keys.size();
    }


}
