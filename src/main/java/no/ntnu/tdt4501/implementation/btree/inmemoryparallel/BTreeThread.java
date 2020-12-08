package no.ntnu.tdt4501.implementation.btree.inmemoryparallel;

import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinitySupport;
import no.ntnu.tdt4501.implementation.btree.BTree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class BTreeThread<K extends Comparable<? super K>, V> extends BTree<K, V> implements Runnable{
    boolean shutdown = false;
    private BPlussTree<K, V> tree;
    private ArrayList<Object[]> items = new ArrayList<>();
    private int numberOfItemsMoved = 0;


    private Thread worker = new Thread(this);


    public BTreeThread(){
        tree = new BPlussTree<>();
        worker.start();
    }

    @Override
    public V search(K key) {
        V value = this.tree.search(key);
        if (value == null){
            try {
                List<Object[]> results;
                synchronized (this) {
                    results = this.items.stream().filter(i -> i[0] == key).collect(Collectors.toList());
                }
                if (results.size() > 0) {
                    return (V) results.get(0)[1];
                }
            }catch (NullPointerException ex){
                return null;
            }
        }
        return value;
    }

    @Override
    public void insert(K key, V value) {
        this.items.add(new Object[]{key, value});
    }

    @Override
    public void delete(K key) {
        V value = tree.root.deleteValue(key, tree.branchingFactor);
        if (value == null){
            try {
                this.items.removeIf(i -> i[0] == key);
            }catch (Exception ignored){
            }
        }
    }

    @Override
    public void run() {
        AffinityLock lock = AffinityLock.acquireLock();
        int threadId = AffinitySupport.getThreadId();
        System.out.println("Thread " + threadId  + "locked to CPU id " + lock.cpuId());
        while (!this.shutdown) {
            if (!this.items.isEmpty()) {
                Object[] item;
                synchronized (this) {
                    item = this.items.remove(this.items.size()-1);
                }
                if (item != null) {
                    this.tree.insert((K)item[0],(V)item[1]);
                }
                numberOfItemsMoved += 1;
            }
        }
        lock.release();
        System.out.println("Moved: " + numberOfItemsMoved + "\n");

        System.out.println("Queue size: " + this.items.size() + "\n");

    }

}
