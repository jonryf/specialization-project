package no.ntnu.tdt4501.implementation.btree.inmemory;

import no.ntnu.tdt4501.implementation.btree.BTree;

public class BPlussTree<K extends Comparable<? super K>, V> extends BTree<K, V> {
    private static final int BRANCHING_FACTOR = 128;

    private int branchingFactor;

    Node<K, V> root;

    public BPlussTree(){
        this(BRANCHING_FACTOR);
    }

    public BPlussTree(int branchingFactor) {
        this.branchingFactor = branchingFactor;
        this.root = new LeafNode<>(this);
    }

    @Override
    public synchronized V search(K key) {
        return this.root.getValue(key);
    }

    @Override
    public synchronized void insert(K key, V value) {
        this.root.insertValue(key, value, this.branchingFactor);
    }

    @Override
    public synchronized void delete(K key) {
        this.root.deleteValue(key, this.branchingFactor);
    }

    synchronized void updateRootNode(Node<K, V> node){
        this.root = node;
    }

}
