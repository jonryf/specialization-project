package no.ntnu.tdt4501.implementation.btree.inmemoryparallel;

import no.ntnu.tdt4501.implementation.btree.BTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BPlussTree<K extends Comparable<? super K>, V> extends BTree<K, V> {
    private static final int BRANCHING_FACTOR = 128;

    int branchingFactor;

    volatile Node<K, V> root;

    public BPlussTree(){
        this(BRANCHING_FACTOR);
    }

    public BPlussTree(int branchingFactor) {
        this.branchingFactor = branchingFactor;
        this.root = new LeafNode<>(this);
    }

    @Override
    public V search(K key) {
        return this.root.getValue(key);
    }


    public void bulkInsert(List<Map.Entry<K, V>> items){
        int numberOfLeafNodes = (int)Math.ceil(items.size() /(BRANCHING_FACTOR + 0.0D));
        int itemsPerPage = BRANCHING_FACTOR - (items.size() % (BRANCHING_FACTOR))/numberOfLeafNodes;

        List<K> keys = items.parallelStream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<V> values = items.parallelStream().map(Map.Entry::getValue).collect(Collectors.toList());

        List<Node<K, V>> nodes = new ArrayList<>();

        for(int leafNode = 0; leafNode<numberOfLeafNodes; leafNode ++){
            LeafNode<K, V> node = new LeafNode<>(this);
            int indexFrom = leafNode*itemsPerPage;
            int indexTo = leafNode*itemsPerPage + itemsPerPage;
            node.keys.addAll(keys.subList(indexFrom, indexTo));
            node.values.addAll(values.subList(indexFrom, indexTo));
            nodes.add(node);
        }
        this.updateRootNode(buildFromNodes(nodes));
    }

    private Node<K, V> buildFromNodes(List<Node<K, V>> nodes){
        while(true) {
            List<Node<K, V>> parentLayer = new ArrayList<>();
            System.out.println(nodes.size());
            int parentNodes = (int) Math.ceil(nodes.size() / (BRANCHING_FACTOR  + 0.0D));
            int nodesPerParent = (int) Math.ceil(nodes.size() / parentNodes);

            int max = nodes.size();
            for (int parentNode = 0; parentNode < parentNodes; parentNode++) {
                InternalNode<K, V> node = new InternalNode<>(this);
                List<Node<K, V>> nodesToAdd = nodes.subList(
                        nodesPerParent * parentNode,
                        Math.min(nodesPerParent * parentNode + nodesPerParent, max)
                );
                node.children.addAll(nodesToAdd);
                node.keys.addAll(nodesToAdd.stream().map(n -> n.keys.get(n.keys.size() - 1)).collect(Collectors.toList()));
                parentLayer.add(node);
            }
            if(parentLayer.size() == 1){
                return parentLayer.get(0);
            }
            nodes = parentLayer;
        }
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
