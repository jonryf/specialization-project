package no.ntnu.tdt4501.implementation.btree;

import btree4j.BTreeException;
import btree4j.Value;
import btree4j.utils.io.FileUtils;

import java.io.File;
import java.util.Random;

public class BTree4J extends BTree<String, Long> {
    private btree4j.BTree instance;

    public BTree4J() {
        File tmpDir = FileUtils.getTempDir();
        int randomID = new Random().nextInt(10000);
        File tmpFile = new File(tmpDir, randomID + ".idx");
        this.instance = new btree4j.BTree(tmpFile, false);
        try {
            this.instance.init(false);
        } catch (BTreeException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param indexFile
     */
    public BTree4J(File indexFile) {
        this.instance = new btree4j.BTree(indexFile, false);
        try {
            this.instance.init(false);
        } catch (BTreeException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Long search(String key) {
        try {
            return this.instance.findValue(new Value(key));
        } catch (BTreeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(String key, Long value) {
        try {
            this.instance.addValue(new Value(key), value);
        } catch (BTreeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String key) {
        try {
            this.instance.removeValue(new Value(key));
        } catch (BTreeException e) {
            e.printStackTrace();
        }
    }
}
