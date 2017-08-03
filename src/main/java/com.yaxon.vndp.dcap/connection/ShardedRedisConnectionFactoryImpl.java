package com.yaxon.vndp.dcap.connection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yaxon.vndp.dcap.Shard;
import com.yaxon.vndp.dcap.ShardId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 20:22
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * redis分区连接工厂实现类
 */
public class ShardedRedisConnectionFactoryImpl implements ShardedRedisConnectionFactory{
    private static final Logger logger = LoggerFactory.getLogger(ShardedRedisConnectionFactoryImpl.class);

    private final List<Shard> shards;
    private List<ShardId> shardIds;
    private Map<ShardId, Shard> shardIdToShardMap;

    public ShardedRedisConnectionFactoryImpl(List<Shard> shards) {
        this.shards = shards;
        buildShardIdToShardMap(shards);
    }

    private final void buildShardIdToShardMap(List<Shard> shards) {
        shardIdToShardMap = Maps.newHashMap();
        Set<ShardId> uniqueShardIds = Sets.newHashSet();
        for (Shard shard : shards) {
            for(ShardId shardId : shard.getShardIds()) {
                if(!uniqueShardIds.add(shardId)) {
                    throw new RuntimeException("Cannot have the same shard id: " + shardId);
                }
                shardIdToShardMap.put(shardId, shard);
            }
        }
        shardIds = Lists.newArrayList(uniqueShardIds);
    }

    public List<ShardId> getShardIds() {
        return Collections.<ShardId> unmodifiableList(shardIds);
    }

    public List<Shard> getAllShards() {
        return Collections.<Shard> unmodifiableList(shards);
    }

    public Set<Shard> getShardsByShardIds(List<ShardId> shardIds) {
        Set<Shard> shards = Sets.newHashSet();
        for (ShardId shardId : shardIds) {
            Shard shard = shardIdToShardMap.get(shardId);
            if (shard == null) {
                throw new RuntimeException("Invalid shard id: " + shardId);
            }
            shards.add(shard);
        }
        return shards;
    }
}
