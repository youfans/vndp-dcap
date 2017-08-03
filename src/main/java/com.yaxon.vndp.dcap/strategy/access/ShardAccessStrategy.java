package com.yaxon.vndp.dcap.strategy.access;

import com.yaxon.vndp.dcap.Shard;
import com.yaxon.vndp.dcap.ShardOperation;
import com.yaxon.vndp.dcap.strategy.exit.ExitStrategy;

import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 10:09
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface ShardAccessStrategy {
    <T> T apply(List<Shard> shards, ShardOperation<T> operation,ExitStrategy<T> exitStrategy);
}
