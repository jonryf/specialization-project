package no.ntnu.tdt4501.benchmark.YCSB;

import no.ntnu.tdt4501.implementation.btree.BTree;
import no.ntnu.tdt4501.implementation.btree.inmemorybulk.BulkBPlussTree;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class YcsbHBTree extends DB {
    private BTree<String, Long> instance;


    /**
     * Initialize any state for this DB. Called once per DB instance; there is one
     * DB instance per client thread.
     */
    @Override
    public void init() throws DBException {
        this.instance = new BulkBPlussTree<>();

    }

    @Override
    public void cleanup(){
        if(this.instance instanceof BulkBPlussTree) {
            ((BulkBPlussTree) this.instance).shutdown();
        }
    }

    @Override
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
        //long value = this.instance.search(key);
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

        this.instance.insert(key, 10L);
        return Status.OK;
    }

    @Override
    public Status delete(String table, String key) {
        return Status.NOT_IMPLEMENTED;
    }
}
