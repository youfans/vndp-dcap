package com.yaxon.vndp.dcap.strategy;

import com.yaxon.vndp.dcap.strategy.access.ShardAccessStrategy;
import com.yaxon.vndp.dcap.strategy.resolution.ShardResolutionStrategy;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 11:14
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class DefaultShardStrategyFactory implements ShardStrategyFactory {

    private ShardAccessStrategy shardAccessStrategy;
    private ShardResolutionStrategy shardResolutionStrategy;

    @Override
    public ShardStrategy newShardStrategy() {
        return new ShardStrategy() {
            @Override
            public ShardResolutionStrategy getShardResolutionStrategy() {
                return shardResolutionStrategy;
            }

            @Override
            public ShardAccessStrategy getShardAccessStrategy() {
                return shardAccessStrategy;
            }
        };
    }

    public ShardAccessStrategy getShardAccessStrategy() {
        return shardAccessStrategy;
    }

    public void setShardAccessStrategy(ShardAccessStrategy shardAccessStrategy) {
        this.shardAccessStrategy = shardAccessStrategy;
    }

    public ShardResolutionStrategy getShardResolutionStrategy() {
        return shardResolutionStrategy;
    }

    public void setShardResolutionStrategy(ShardResolutionStrategy shardResolutionStrategy) {
        this.shardResolutionStrategy = shardResolutionStrategy;
    }
}
