package com.yaxon.vndp.dcap;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 09:36
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface ShardOperation<T> {
    T execute(Shard shard);
    String getOperationName();
}
