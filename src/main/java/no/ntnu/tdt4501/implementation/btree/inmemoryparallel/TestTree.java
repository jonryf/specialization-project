package no.ntnu.tdt4501.implementation.btree.inmemoryparallel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTree {

    public static void main2(String[] args) {
        BPlussTree<Integer, Integer> tree = new BPlussTree<>();
        long start = System.currentTimeMillis();

        int nItems = 10000000;
        BPlussTree<Integer, Integer> items = new BPlussTree<>();
        for(int i = 0; i<nItems; i++){
            items.insert(i, i);
        }
        System.out.println("Map insert done" + (System.currentTimeMillis()-start)/1000D);
        List<Map.Entry<Integer, Integer>> sorted = null; //items.entrySet()
                //.parallelStream().collect(Collectors.toList());
                //.sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
        System.out.println("Sort done" + (System.currentTimeMillis()-start)/1000D);
        tree.bulkInsert(sorted);
        System.out.println("B-tree insert done" + (System.currentTimeMillis()-start)/1000D);

        long end = System.currentTimeMillis();
        System.out.println((end-start)/1000D);
        System.out.println(nItems/((end-start)/1000D));

        System.out.println(tree.search(100));
    }

    public static void main(String[] args) {
        BPlussTree<Integer, Integer> tree = new BPlussTree<>();
        long start = System.currentTimeMillis();

        int nItems = 10000000;
        HashMap<Integer, Integer> items = new HashMap<>();
        for(int i = 0; i<nItems; i++){
            tree.insert(i, i);
        }
        long end = System.currentTimeMillis();
        System.out.println((end-start)/1000D);
        System.out.println(nItems/((end-start)/1000D));

        System.out.println(tree.search(100));
    }
}
