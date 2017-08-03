package com.yaxon.vndp.dcap.strategy.exit;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 15:39
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class FirstNonNullResultExitStrategy<T> implements ExitStrategy<T> {

    private T nonNullResult;

    @Override
    public synchronized boolean addResult(T result) {
        if (result != null && nonNullResult == null) {
            nonNullResult = result;
            return true;
        }
        return false;
    }

    @Override
    public T compileResults() {
        return nonNullResult;
    }
}
