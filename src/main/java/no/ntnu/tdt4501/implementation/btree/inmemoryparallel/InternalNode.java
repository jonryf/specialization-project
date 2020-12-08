package no.ntnu.tdt4501.implementation.btree.inmemoryparallel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal node in a B+-tree.
 *
 * Implements a global lock, per object, for all actions that modify the state of the node.
 *
 * @param <K> key parameter type
 * @param <V> value parameter type
 */
@SuppressWarnings("Duplicates")
public class InternalNode <K extends Comparable<? super K>, V> extends Node<K, V> {
    volatile List<Node<K, V>> children;
    private final BPlussTree<K,V> tree;

    InternalNode(BPlussTree<K, V> tree) {
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.tree = tree;
    }


    @Override
    public V getValue(K key) {
        return getChild(key).getValue(key);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public V deleteValue(K key, int branchingFactor) {
        Node<K, V> child = getChild(key);
        V value = child.deleteValue(key, branchingFactor);

        if (child.isUnderflow(branchingFactor)) {
            Node<K, V> childLeftSibling = getChildLeftSibling(key);
            Node<K, V> childRightSibling = getChildRightSibling(key);
            Node<K, V> left = childLeftSibling != null ? childLeftSibling : child;
            Node<K, V> right = childLeftSibling != null ? child
                    : childRightSibling;

            left.merge(right);
            this.deleteChild(right.getFirstLeafKey());
            if (left.isOverflow(branchingFactor)) {
                Node<K, V> sibling = left.split();
                insertChild(sibling.getFirstLeafKey(), sibling);
            }
            if (this.tree.root.keyNumber() == 0) {
                this.tree.updateRootNode(left);
            }
        }
        return value;

    }


    @SuppressWarnings("Duplicates")
    @Override
    public void insertValue(K key, V value, int branchingFactor) {
        Node<K, V> child = getChild(key);
        child.insertValue(key, value, branchingFactor);

        if (child.isOverflow(branchingFactor)) {
            Node<K, V> sibling = child.split();
            insertChild(sibling.getFirstLeafKey(), sibling);
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
        return this.children.get(0).getFirstLeafKey();
    }

    @Override
    public boolean isOverflow(int branchingFactor) {
        return children.size() > branchingFactor;
    }

    @Override
    public boolean isUnderflow(int branchingFactor) {
        return children.size() < (branchingFactor + 1) / 2;
    }

    @Override
    public int keyNumber() {
        return this.keys.size();
    }

    @Override
    public synchronized void merge(Node sibling) {
        @SuppressWarnings("unchecked")
        InternalNode<K, V> node = (InternalNode) sibling;
        keys.add(node.getFirstLeafKey());
        keys.addAll(node.keys);
        children.addAll(node.children);
    }

    @Override
    public synchronized Node<K, V> split() {
        int from = keyNumber() / 2 + 1, to = keyNumber();
        InternalNode<K, V> sibling = new InternalNode<>(this.tree);
        sibling.keys.addAll(keys.subList(from, to));
        sibling.children.addAll(children.subList(from, to + 1));
        keys.subList(from - 1, to).clear();
        children.subList(from, to + 1).clear();
        return sibling;
    }

    private Node<K, V> getChild(K key) {
        int loc = Collections.binarySearch(this.keys, key);
        int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
        return children.get(childIndex);
    }

    private void deleteChild(K key) {
        int loc = Collections.binarySearch(this.keys, key);
        if (loc >= 0) {
            keys.remove(loc);
            children.remove(loc + 1);
        }
    }

    private void insertChild(K key, Node<K, V> child) {
        int loc = Collections.binarySearch(keys, key);
        int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
        if (loc >= 0) {
            children.set(childIndex, child);
        } else {
            keys.add(childIndex, key);
            children.add(childIndex + 1, child);
        }
    }

    private Node<K, V> getChildLeftSibling(K key) {
        int loc = Collections.binarySearch(keys, key);
        int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
        if (childIndex > 0) {
            return children.get(childIndex - 1);
        }
        return null;
    }

    private Node<K, V> getChildRightSibling(K key) {
        int loc = Collections.binarySearch(keys, key);
        int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
        if (childIndex < keys.size()) {
            return children.get(childIndex + 1);
        }
        return null;
    }
}
