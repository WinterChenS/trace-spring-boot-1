package cn.lastwhisper.trace.common.collection;

import cn.lastwhisper.trace.event.CascadeEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 固定大小lru策略淘汰数据的{@link LinkedHashMap} 实现
 *
 * @author lastwhisper
 * @date 2020/3/11
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class CascadeLRUCacheMap<K, V> extends LinkedHashMap<K, V> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 容量，超过此容量自动删除末尾元素 */
    private int capacity;
    /** 缓冲大小 */
    private Integer bufferSize;
    /** 缓冲区 */
    private List<K> buffers;
    /** 级联事件推送者 */
    private CascadeEventPublisher<K> publisher;

    /**
     *
     * @param capacity LRU大小
     * @param bufferSize 缓冲区大小
     */
    public CascadeLRUCacheMap(int capacity, Integer bufferSize, CascadeEventPublisher<K> cascadeEventPublisher) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
        this.bufferSize = bufferSize;
        buffers = new ArrayList<>(bufferSize);
        publisher = cascadeEventPublisher;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean flag = size() > capacity;
        if (flag) {
            buffers.add(eldest.getKey());
            if (buffers.size() >= bufferSize) {
                logger.debug("Trigger delete buffer:{}", buffers.toString());
                publisher.sendRemoveMessage(new ArrayList<>(buffers));
                buffers.clear();
            }
            logger.debug("There are still {} triggers to delete the buffer. buffer traceId:{} buffer Size:{}",
                    bufferSize - buffers.size(), eldest.getKey(), buffers.size());
        }
        return flag;
    }


}
