package cn.lastwhisper.trace.common.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 固定大小lru策略淘汰数据的{@link LinkedHashMap} 实现
 *
 *  同步lru缓存
 *
 * @author lastwhisper
 * @date 2020/3/11
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class ConcurrentLruCacheMap<K, V> extends LinkedHashMap<K, V> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    /** 容量，超过此容量自动删除末尾元素 */
    private int capacity;

    public ConcurrentLruCacheMap(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean flag = size() > capacity;
        if (flag) {
            logger.debug("remove k:{},v:{}", eldest.getKey(), eldest.getValue());
        }
        return flag;
    }

    /**
     * 同步写
     *
     * @param k k
     * @param v v
     */
    public V syncPut(K k, V v) {
        writeLock.lock();
        try {
            return this.put(k, v);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 同步读
     *
     * @param k k
     */
    public V syncGet(K k) {
        readLock.lock();
        try {
            return this.get(k);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 同步写
     */
    public void syncClear() {
        writeLock.lock();
        try {
            this.clear();
        } finally {
            writeLock.unlock();
        }
    }

}
