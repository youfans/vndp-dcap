package com.yaxon.vndp.dcap.strategy.exit;

import com.yaxon.vndp.dcap.Shard;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 15:13
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface ExitStrategy<T> {
    boolean addResult(T result);
    T compileResults();
}
