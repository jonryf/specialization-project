package no.ntnu.tdt4501.implementation.btree.inmemorybulk;

import java.util.List;
import java.util.Map;

/**
 * A node in a B+-tree
 *
 * @param <K> type parameter for key
 * @param <V> type parameter for value
 */
abstract class Node<K, V> {
    volatile List<K> keys;

    /**
     * Search for a value in this node, or any of its child nodes.
     *
     * @param key key to search for
     * @return search result. Null if not present, or null value is stored
     */
    abstract V getValue(K key);

    /**
     * Delete a key value pair if exist
     *
     * @param key key for pair
     * @param branchingFactor branching factor in the given B-tree
     */
    abstract void deleteValue(K key, int branchingFactor);

    /**
     * Insert a key value pair to the node
     *
     * @param key key for the pair
     * @param value value for the pair
     * @param branchingFactor branching factor in the given B-tree
     */
    abstract void insertValue(K key, V value, int branchingFactor);

    /**
     * Get first leafkey for this particular node
     *
     * @return first leaf key
     */
    abstract K getFirstLeafKey();

    /**
     * Merge this node with a another node
     *
     * @param sibling node to merge with
     */
    abstract void merge(Node sibling);

    /**
     * Split this node in two different nodes
     *
     * @return parent to spliced node
     */
    abstract Node<K, V> split();

    /**
     * Check if to too many subnodes/values for this node
     *
     * @param branchingFactor for the given B-tree
     * @return true if overflow
     */
    abstract boolean isOverflow(int branchingFactor);

    /**
     * Is too few subnodes/values left in the node
     *
     * @param branchingFactor for the given B-tree
     * @return true if underflow
     */
    abstract boolean isUnderflow(int branchingFactor);

    /**
     * Key number of keys in the node
     *
     * @return number of keys
     */
    abstract int keyNumber();


}
