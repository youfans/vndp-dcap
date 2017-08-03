package com.yaxon.vndp.dcap.strategy.resolution;

import com.yaxon.vndp.dcap.ShardId;

import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:24
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 分区（Shard）解析策略。
 * 通过执行接口的命名空间或执行命令常量参数、以及车辆id来判断，该操作在哪些redis分区进行执行。
 */
public interface ShardResolutionStrategy {
    /**
     * 根据分区解析决策数据来判断数据访问操作再哪些redis分区执行
     *
     * @param shardResolutionStrategyData 分区解析决策数据
     * @return 分区ID列表
     */
    List<ShardId> selectShardIdsFromShardResolutionStrategyData(
            ShardResolutionStrategyData shardResolutionStrategyData);
}
