package cn.lastwhisper.trace;

import cn.lastwhisper.trace.common.collection.CascadeLRUCacheMap;
import cn.lastwhisper.trace.common.collection.LRUCacheMap;
import cn.lastwhisper.trace.event.CascadeEventPublisher;
import cn.lastwhisper.trace.event.CascadeIdNotifier;
import cn.lastwhisper.trace.model.Node;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.*;


@Configuration
@ConditionalOnWebApplication
@ComponentScan
public class ProjectAutoConfiguration {

    /**
     * 单线程池
     */
    @Bean
    public ExecutorService singleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
                System.out.println("Task discarded queue size:" + executor.getQueue().size());
            }
        });
    }

    @Bean
    public Map<Long, Node> nodeIdMap() {
        return new LRUCacheMap<>(2000);
    }

    @Bean
    public Map<Long, Node> traceIdMap() {
        return new CascadeLRUCacheMap<>(100, 10, cascadeEventPublisher());
    }

    /**
     * 消息事件推送者
     */
    @Bean
    public CascadeEventPublisher<Long> cascadeEventPublisher() {
        return new CascadeEventPublisher<>();
    }

}
