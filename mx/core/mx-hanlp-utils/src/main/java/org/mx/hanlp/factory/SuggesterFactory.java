package org.mx.hanlp.factory;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.factory.suggest.SuggestContentProvider;
import org.mx.hanlp.impl.ItemSuggesterImpl;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 描述： 推荐工厂
 *
 * @author John.Peng
 * Date time 2018/4/16 下午5:25
 */
public class SuggesterFactory {
    private static final Log logger = LogFactory.getLog(SuggesterFactory.class);

    private Map<String, ItemSuggester> suggesters;
    private Map<String, SuggesterStat> stats;
    private ExecutorService executor = null;
    private List<Future<Long>> futures = null;
    private Timer cleanTimer;

    private Environment env;

    /**
     * 默认的构造函数
     *
     * @param env Spring IoC上下文环境
     */
    public SuggesterFactory(Environment env) {
        super();
        this.env = env;
        this.suggesters = new HashMap<>();
        this.stats = new HashMap<>();
    }

    /**
     * 获取系统中存在的默认推荐器，其类型为<pre>{@link ItemSuggester.SuggestItem#DEFAULT_TYPE}</pre>。
     *
     * @return 推荐器，如果不存在，则返回null。
     * @see ItemSuggester.SuggestItem#DEFAULT_TYPE
     */
    public ItemSuggester getSuggester() {
        return getSuggester(ItemSuggester.SuggestItem.DEFAULT_TYPE);
    }

    /**
     * 获取系统中存在的推荐器
     *
     * @param type 指定的推荐器类型
     * @return 推荐器，如果不存在，则返回null。
     */
    public ItemSuggester getSuggester(String type) {
        if (suggesters.containsKey(type)) {
            return suggesters.get(type);
        }
        return null;
    }

    /**
     * 获取推荐系统中的统计信息
     *
     * @return 统计信息列表
     */
    public Collection<SuggesterStat> getSuggesterStat() {
        return stats.values();
    }

    /**
     * 重新刷新并加载推荐数据
     */
    public void reloadSuggesters() {
        if (executor != null && !(executor.isShutdown() || executor.isTerminated())) {
            // 已经有加载任务了，忽略本次重载请求
            if (logger.isWarnEnabled()) {
                logger.warn("Any reload task is running, will be ignored.");
            }
            return;
        }
        executor = Executors.newFixedThreadPool(suggesters.size());
        futures = new ArrayList<>();
        for (final String type : suggesters.keySet()) {
            Future<Long> future = executor.submit(() -> {
                ItemSuggester suggester = suggesters.get(type);
                SuggesterStat stat = stats.get(type);
                stat.setLastStartTime(System.currentTimeMillis());
                stats.put(type, stat);
                long total = suggester.reload();
                stat.setLastFinishTime(System.currentTimeMillis());
                stat.setReloadTotal(total);
                stat.setItemTotal(suggester.getTotal());
                stats.put(type, stat);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("[%s] task finished, data: %s.", type, JSON.toJSONString(stat)));
                }
                return total;
            });
            futures.add(future);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Start the load task successfully.");
        }
    }

    /**
     * 清除所有的推荐内容
     */
    public void clear() {
        if (cleanTimer != null) {
            cleanTimer.cancel();
            cleanTimer.purge();
            cleanTimer = null;
        }
        suggesters.forEach((type, suggester) -> suggester.clear());
        if (logger.isDebugEnabled()) {
            logger.debug("All suggester's data be cleared.");
        }
    }

    /**
     * 推荐器工厂是否已经就绪
     *
     * @return 返回true表示就绪，可以提供服务；否则返回false。
     */
    public boolean ready() {
        if (futures != null && !futures.isEmpty()) {
            for (Future<Long> future : futures) {
                if (!future.isDone()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 销毁推荐工厂
     */
    public void destroy() {
        if (cleanTimer != null) {
            cleanTimer.cancel();
            cleanTimer.purge();
        }
        if (executor != null) {
            executor.shutdownNow();
        }
        if (suggesters != null && !suggesters.isEmpty()) {
            for (ItemSuggester suggester : suggesters.values()) {
                suggester.close();
            }
            suggesters.clear();
            suggesters = null;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Destroy the suggester factory successfully.");
        }
    }

    /**
     * 初始化推荐工厂
     *
     * @throws Exception 初始化过程中发生的异常
     */
    public void init() throws Exception {
        int num = env.getProperty("suggester.num", Integer.class, 0);
        if (num <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("There not config any suggester.");
            }
            return;
        }
        int total = 0;
        for (int index = 1; index <= num; index++) {
            String type = env.getProperty(String.format("suggester.%d.type", index)),
                    clazzName = env.getProperty(String.format("suggester.%d.provider", index));
            if (StringUtils.isBlank(type)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The type is blank for the suggester, it will be ignored.");
                }
                continue;
            }
            SuggestContentProvider provider = null;
            if (StringUtils.isBlank(clazzName)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The class's name maybe blank for the suggester.");
                }
            } else {
                provider = (SuggestContentProvider) Class.forName(clazzName).newInstance();
                provider.initEnvironment(env, String.format("suggester.%d.config", index));
            }
            ItemSuggester itemSuggester = new ItemSuggesterImpl(type, provider);
            suggesters.put(type, itemSuggester);
            stats.put(type, new SuggesterStat(type));
            total++;
        }
        // 启动清理定时器，启动后3秒开始，每隔30秒检测一次
        cleanTimer = new Timer();
        cleanTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ready() && futures != null) {
                    futures.clear();
                    futures = null;
                    executor.shutdownNow();
                    executor = null;
                    if (logger.isDebugEnabled()) {
                        logger.debug("All load task are finished.");
                    }
                }
            }
        }, 3000, 30000);
        // 启动一次刷新任务
        reloadSuggesters();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Initialize %d suggester successfully.", total));
        }
    }
}
