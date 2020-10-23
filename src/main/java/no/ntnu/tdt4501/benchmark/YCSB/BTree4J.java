package no.ntnu.tdt4501.benchmark.YCSB;

import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;
import site.ycsb.StringByteIterator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class BTree4J extends DB {
    private no.ntnu.tdt4501.implementation.btree.BTree4J instance;


    /**
     * Initialize any state for this DB. Called once per DB instance; there is one
     * DB instance per client thread.
     */
    @Override
    public void init() throws DBException {
        this.instance = new no.ntnu.tdt4501.implementation.btree.BTree4J();

    }

    @Override
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
        long value = this.instance.search(key);
        return Status.OK;
    }

    @Override
    public Status scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
        return Status.NOT_IMPLEMENTED;
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {
        return Status.NOT_IMPLEMENTED;
    }

    @Override
    public Status insert(String table, String key, Map<String, ByteIterator> values) {
        StringBuilder value = new StringBuilder();

        for (Map.Entry<String, String> entry : StringByteIterator.getStringMap(values).entrySet()) {
            value.append(";").append(entry.getValue()).append(":").append(entry.getValue());
        }
        this.instance.insert(key, (long) value.hashCode());
        return Status.OK;
    }

    @Override
    public Status delete(String table, String key) {
        this.instance.delete(key);

        return Status.OK;
    }
}
