package com.yaxon.vndp.dcap.connection;

import com.yaxon.vndp.dcap.Shard;
import com.yaxon.vndp.dcap.ShardId;

import java.util.List;
import java.util.Set;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 19:34
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * redis分区连接工厂接口
 */
public interface ShardedRedisConnectionFactory {
    /**
     * 获取所有分区id集合
     * @return
     */
    List<ShardId> getShardIds();

    /**
     * 获取所有分区对象集合
     * @return
     */
    List<Shard> getAllShards();

    /**
     * 根据分区id集合获得对应的分区对象集合
     * @param shardIds
     * @return
     */
    Set<Shard> getShardsByShardIds(List<ShardId> shardIds);
}
