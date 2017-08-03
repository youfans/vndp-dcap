package com.yaxon.vndp.dcap.strategy;

import com.yaxon.vndp.dcap.strategy.access.ShardAccessStrategy;
import com.yaxon.vndp.dcap.strategy.resolution.ShardResolutionStrategy;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:07
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface ShardStrategy {
    ShardResolutionStrategy getShardResolutionStrategy();
    ShardAccessStrategy getShardAccessStrategy();
}
