package no.ntnu.tdt4501.implementation.btree.inmemory;

public interface Node<K, V> {

    V getValue(K key);

    void deleteValue(K key, int branchingFactor);

    void insertValue(K key, V value, int branchingFactor);

    K getFirstLeafKey();

    void merge(Node sibling);

    Node<K, V> split();

    boolean isOverflow(int branchingFactor);

    boolean isUnderflow(int branchingFactor);

    int keyNumber();
}
