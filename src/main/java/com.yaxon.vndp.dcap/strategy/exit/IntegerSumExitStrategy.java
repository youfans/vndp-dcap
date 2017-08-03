package com.yaxon.vndp.dcap.strategy.exit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 17:31
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class IntegerSumExitStrategy implements ExitStrategy<Integer> {
    private AtomicInteger sum = new AtomicInteger(0);

    @Override
    public boolean addResult(Integer result) {
        sum.addAndGet(result);
        return false;
    }

    @Override
    public Integer compileResults() {
        return sum.get();
    }
}
