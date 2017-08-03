package com.yaxon.vndp.dcap;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Set;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 19:45
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 分区接口
 */
public interface Shard {
    StringRedisTemplate getRedisTemplate();
    RedisMessageListenerContainer getRedisMessageListenerContainer();
    Set<ShardId> getShardIds();
}
