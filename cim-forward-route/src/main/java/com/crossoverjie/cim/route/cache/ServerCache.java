package com.crossoverjie.cim.route.cache;

import com.crossoverjie.cim.route.kit.ZKit;
import com.google.common.cache.LoadingCache;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Function: 服务器节点缓存
 *
 * @author crossoverJie Date: 2018/8/19 01:31
 * @since JDK 1.8
 */
@Component
public class ServerCache {


    @Autowired
    private LoadingCache<String, String> cache;

    @Autowired
    private ZKit zkUtil;

    private AtomicLong index = new AtomicLong();


    public void addCache(String key) {
        cache.put(key, key);
    }

    @PostConstruct
    public void init() {
        cache.put("127.0.0.1:11211:8081", "127.0.0.1:11211:8081");
    }


    /**
     * 更新所有缓存/先删除 再新增
     */
    public void updateCache(List<String> currentChilds) {
//        cache.invalidateAll();
//        for (String currentChild : currentChilds) {
//            String key = currentChild.split("-")[1];
//            addCache(key);
//        }
    }


    /**
     * 获取所有的服务列表
     */
    public List<String> getAll() {

        List<String> list = new ArrayList<>();

        if (cache.size() == 0) {
            List<String> allNode = zkUtil.getAllNode();
            for (String node : allNode) {
                String key = node.split("-")[1];
                addCache(key);
            }
        }
        for (Map.Entry<String, String> entry : cache.asMap().entrySet()) {
            list.add(entry.getKey());
        }
        return list;

    }

    /**
     * 选取服务器
     */
    public String selectServer() {
        List<String> all = getAll();
        if (all.size() == 0) {
            throw new RuntimeException("CIM 服务器可用服务列表为空");
        }
        Long position = index.incrementAndGet() % all.size();
        if (position < 0) {
            position = 0L;
        }

        return all.get(position.intValue());
    }
}
