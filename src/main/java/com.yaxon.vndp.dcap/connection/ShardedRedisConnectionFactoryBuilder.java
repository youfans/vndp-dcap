package com.yaxon.vndp.dcap.connection;

import com.google.common.collect.Lists;
import com.yaxon.vndp.dcap.Shard;
import com.yaxon.vndp.dcap.ShardImpl;
import com.yaxon.vndp.dcap.jedis.JedisConnectionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 19:31
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * redis分区构建者类
 */
public class ShardedRedisConnectionFactoryBuilder implements FactoryBean<ShardedRedisConnectionFactory>, InitializingBean {
    private Map<JedisConnectionFactory, String> redisSources;
    private ShardedRedisConnectionFactory shardedSqlConnectionFactory;


    @Override
    public ShardedRedisConnectionFactory getObject() throws Exception {
        return this.shardedSqlConnectionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return ShardedRedisConnectionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Shard> shards = Lists.newArrayList();
        for(Map.Entry<JedisConnectionFactory, String> entry : redisSources.entrySet()){
            JedisConnectionFactory jedisConnectionFactory = entry.getKey();
            String shardIds = entry.getValue();
            shards.add(new ShardImpl(jedisConnectionFactory, shardIds));
        }
        this.shardedSqlConnectionFactory = new ShardedRedisConnectionFactoryImpl(shards);
    }

    public Map<JedisConnectionFactory, String> getRedisSources() {
        return redisSources;
    }

    public void setRedisSources(Map<JedisConnectionFactory, String> redisSources) {
        this.redisSources = redisSources;
    }

    public ShardedRedisConnectionFactory getShardedSqlConnectionFactory() {
        return shardedSqlConnectionFactory;
    }

    public void setShardedSqlConnectionFactory(ShardedRedisConnectionFactory shardedSqlConnectionFactory) {
        this.shardedSqlConnectionFactory = shardedSqlConnectionFactory;
    }
}
