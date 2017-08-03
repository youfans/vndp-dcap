package com.yaxon.vndp.dcap;

import com.yaxon.vndp.dcap.jedis.JedisConnectionFactory;
import com.yaxon.vndp.dcap.util.ShardIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Set;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 19:53
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 分区接口实现类
 */
public class ShardImpl implements Shard {
    private static final Logger logger = LoggerFactory.getLogger(ShardImpl.class);

    private final StringRedisTemplate jedisTemplate;
    private final RedisMessageListenerContainer jedisListenerContainer;
    private final Set<ShardId> shardIds;
    private final int hashCode;

    public ShardImpl(JedisConnectionFactory jedisConnectionFactory, String shardIdSet) {
        jedisTemplate=new StringRedisTemplate(jedisConnectionFactory);
        jedisListenerContainer=new RedisMessageListenerContainer();
        jedisListenerContainer.setConnectionFactory(jedisConnectionFactory);
        jedisListenerContainer.start();
        jedisListenerContainer.afterPropertiesSet();
        this.shardIds = ShardIdUtil.parseShardIds(shardIdSet);
        this.hashCode = shardIds.hashCode();
    }


    public StringRedisTemplate getRedisTemplate() {
        return this.jedisTemplate;
    }

    @Override
    public RedisMessageListenerContainer getRedisMessageListenerContainer() {
        return this.jedisListenerContainer;
    }

    public Set<ShardId> getShardIds() {
        return this.shardIds;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShardImpl)) {
            return false;
        }

        final ShardImpl shard = (ShardImpl) o;

        if (!shardIds.equals(shard.shardIds)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return "Shard-" + shardIds;
    }
}
