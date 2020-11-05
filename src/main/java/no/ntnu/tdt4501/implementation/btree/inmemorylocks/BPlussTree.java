package no.ntnu.tdt4501.implementation.btree.inmemorylocks;

import no.ntnu.tdt4501.implementation.btree.BTree;

public class BPlussTree<K extends Comparable<? super K>, V> extends BTree<K, V> {
    private static final int BRANCHING_FACTOR = 128;

    private int branchingFactor;

    Node<K, V> root;

    public BPlussTree(){
        this(BRANCHING_FACTOR);
        System.out.println("New instance");
    }

    public BPlussTree(int branchingFactor) {
        this.branchingFactor = branchingFactor;
        this.root = new LeafNode<>(this);
    }

    @Override
    public V search(K key) {
        return this.root.getValue(key);
    }

    @Override
    public void insert(K key, V value) {
        this.root.insertValue(key, value, this.branchingFactor);
    }

    @Override
    public void delete(K key) {
        this.root.deleteValue(key, this.branchingFactor);
    }

    synchronized void updateRootNode(Node<K, V> node){
        this.root = node;
    }

}
