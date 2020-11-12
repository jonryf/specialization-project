package no.ntnu.tdt4501.implementation.btree;


/**
 * Generic B-tree
 *
 * @param <I> key type
 * @param <T> value type
 */
public abstract class BTree<I, T> {

    /**
     * Search the B-tree for a given key
     *
     * @param key key to search for
     * @return value in B-tree, given the key
     */
    public abstract T search(I key);


    /**
     * Insert a new key value pair to the B-tree
     *
     * @param key   key to add
     * @param value value to add
     */
    public abstract void insert(I key, T value);

    /**
     * Delete a given key value pair
     *
     * @param key key to the pair
     */
    public abstract void delete(I key);

    public void shutdown(){

    }

}
