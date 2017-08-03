package com.yaxon.vndp.dcap.strategy;

import com.yaxon.vndp.dcap.ShardId;

import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:22
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface ShardStrategyFactory {
    ShardStrategy newShardStrategy();
}
